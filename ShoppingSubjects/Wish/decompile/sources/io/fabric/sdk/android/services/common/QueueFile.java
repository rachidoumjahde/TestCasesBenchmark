package io.fabric.sdk.android.services.common;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.NoSuchElementException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class QueueFile implements Closeable {
    private static final Logger LOGGER = Logger.getLogger(QueueFile.class.getName());
    private final byte[] buffer = new byte[16];
    private int elementCount;
    int fileLength;
    private Element first;
    private Element last;
    /* access modifiers changed from: private */
    public final RandomAccessFile raf;

    static class Element {
        static final Element NULL = new Element(0, 0);
        final int length;
        final int position;

        Element(int i, int i2) {
            this.position = i;
            this.length = i2;
        }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append(getClass().getSimpleName());
            sb.append("[");
            sb.append("position = ");
            sb.append(this.position);
            sb.append(", length = ");
            sb.append(this.length);
            sb.append("]");
            return sb.toString();
        }
    }

    private final class ElementInputStream extends InputStream {
        private int position;
        private int remaining;

        private ElementInputStream(Element element) {
            this.position = QueueFile.this.wrapPosition(element.position + 4);
            this.remaining = element.length;
        }

        public int read(byte[] bArr, int i, int i2) throws IOException {
            QueueFile.nonNull(bArr, "buffer");
            if ((i | i2) < 0 || i2 > bArr.length - i) {
                throw new ArrayIndexOutOfBoundsException();
            } else if (this.remaining <= 0) {
                return -1;
            } else {
                if (i2 > this.remaining) {
                    i2 = this.remaining;
                }
                QueueFile.this.ringRead(this.position, bArr, i, i2);
                this.position = QueueFile.this.wrapPosition(this.position + i2);
                this.remaining -= i2;
                return i2;
            }
        }

        public int read() throws IOException {
            if (this.remaining == 0) {
                return -1;
            }
            QueueFile.this.raf.seek((long) this.position);
            int read = QueueFile.this.raf.read();
            this.position = QueueFile.this.wrapPosition(this.position + 1);
            this.remaining--;
            return read;
        }
    }

    public interface ElementReader {
        void read(InputStream inputStream, int i) throws IOException;
    }

    public QueueFile(File file) throws IOException {
        if (!file.exists()) {
            initialize(file);
        }
        this.raf = open(file);
        readHeader();
    }

    private static void writeInt(byte[] bArr, int i, int i2) {
        bArr[i] = (byte) (i2 >> 24);
        bArr[i + 1] = (byte) (i2 >> 16);
        bArr[i + 2] = (byte) (i2 >> 8);
        bArr[i + 3] = (byte) i2;
    }

    private static void writeInts(byte[] bArr, int... iArr) {
        int i = 0;
        for (int writeInt : iArr) {
            writeInt(bArr, i, writeInt);
            i += 4;
        }
    }

    private static int readInt(byte[] bArr, int i) {
        return ((bArr[i] & 255) << 24) + ((bArr[i + 1] & 255) << 16) + ((bArr[i + 2] & 255) << 8) + (bArr[i + 3] & 255);
    }

    private void readHeader() throws IOException {
        this.raf.seek(0);
        this.raf.readFully(this.buffer);
        this.fileLength = readInt(this.buffer, 0);
        if (((long) this.fileLength) > this.raf.length()) {
            StringBuilder sb = new StringBuilder();
            sb.append("File is truncated. Expected length: ");
            sb.append(this.fileLength);
            sb.append(", Actual length: ");
            sb.append(this.raf.length());
            throw new IOException(sb.toString());
        }
        this.elementCount = readInt(this.buffer, 4);
        int readInt = readInt(this.buffer, 8);
        int readInt2 = readInt(this.buffer, 12);
        this.first = readElement(readInt);
        this.last = readElement(readInt2);
    }

    private void writeHeader(int i, int i2, int i3, int i4) throws IOException {
        writeInts(this.buffer, i, i2, i3, i4);
        this.raf.seek(0);
        this.raf.write(this.buffer);
    }

    private Element readElement(int i) throws IOException {
        if (i == 0) {
            return Element.NULL;
        }
        this.raf.seek((long) i);
        return new Element(i, this.raf.readInt());
    }

    /* JADX INFO: finally extract failed */
    private static void initialize(File file) throws IOException {
        StringBuilder sb = new StringBuilder();
        sb.append(file.getPath());
        sb.append(".tmp");
        File file2 = new File(sb.toString());
        RandomAccessFile open = open(file2);
        try {
            open.setLength(4096);
            open.seek(0);
            byte[] bArr = new byte[16];
            writeInts(bArr, 4096, 0, 0, 0);
            open.write(bArr);
            open.close();
            if (!file2.renameTo(file)) {
                throw new IOException("Rename failed!");
            }
        } catch (Throwable th) {
            open.close();
            throw th;
        }
    }

    private static RandomAccessFile open(File file) throws FileNotFoundException {
        return new RandomAccessFile(file, "rwd");
    }

    /* access modifiers changed from: private */
    public int wrapPosition(int i) {
        return i < this.fileLength ? i : (i + 16) - this.fileLength;
    }

    private void ringWrite(int i, byte[] bArr, int i2, int i3) throws IOException {
        int wrapPosition = wrapPosition(i);
        if (wrapPosition + i3 <= this.fileLength) {
            this.raf.seek((long) wrapPosition);
            this.raf.write(bArr, i2, i3);
            return;
        }
        int i4 = this.fileLength - wrapPosition;
        this.raf.seek((long) wrapPosition);
        this.raf.write(bArr, i2, i4);
        this.raf.seek(16);
        this.raf.write(bArr, i2 + i4, i3 - i4);
    }

    /* access modifiers changed from: private */
    public void ringRead(int i, byte[] bArr, int i2, int i3) throws IOException {
        int wrapPosition = wrapPosition(i);
        if (wrapPosition + i3 <= this.fileLength) {
            this.raf.seek((long) wrapPosition);
            this.raf.readFully(bArr, i2, i3);
            return;
        }
        int i4 = this.fileLength - wrapPosition;
        this.raf.seek((long) wrapPosition);
        this.raf.readFully(bArr, i2, i4);
        this.raf.seek(16);
        this.raf.readFully(bArr, i2 + i4, i3 - i4);
    }

    public void add(byte[] bArr) throws IOException {
        add(bArr, 0, bArr.length);
    }

    public synchronized void add(byte[] bArr, int i, int i2) throws IOException {
        int i3;
        nonNull(bArr, "buffer");
        if ((i | i2) >= 0) {
            if (i2 <= bArr.length - i) {
                expandIfNecessary(i2);
                boolean isEmpty = isEmpty();
                if (isEmpty) {
                    i3 = 16;
                } else {
                    i3 = wrapPosition(this.last.position + 4 + this.last.length);
                }
                Element element = new Element(i3, i2);
                writeInt(this.buffer, 0, i2);
                ringWrite(element.position, this.buffer, 0, 4);
                ringWrite(element.position + 4, bArr, i, i2);
                writeHeader(this.fileLength, this.elementCount + 1, isEmpty ? element.position : this.first.position, element.position);
                this.last = element;
                this.elementCount++;
                if (isEmpty) {
                    this.first = this.last;
                }
            }
        }
        throw new IndexOutOfBoundsException();
    }

    public int usedBytes() {
        if (this.elementCount == 0) {
            return 16;
        }
        if (this.last.position >= this.first.position) {
            return (this.last.position - this.first.position) + 4 + this.last.length + 16;
        }
        return (((this.last.position + 4) + this.last.length) + this.fileLength) - this.first.position;
    }

    private int remainingBytes() {
        return this.fileLength - usedBytes();
    }

    public synchronized boolean isEmpty() {
        return this.elementCount == 0;
    }

    private void expandIfNecessary(int i) throws IOException {
        int i2 = i + 4;
        int remainingBytes = remainingBytes();
        if (remainingBytes < i2) {
            int i3 = this.fileLength;
            do {
                remainingBytes += i3;
                i3 <<= 1;
            } while (remainingBytes < i2);
            setLength(i3);
            int wrapPosition = wrapPosition(this.last.position + 4 + this.last.length);
            if (wrapPosition < this.first.position) {
                FileChannel channel = this.raf.getChannel();
                channel.position((long) this.fileLength);
                long j = (long) (wrapPosition - 4);
                if (channel.transferTo(16, j, channel) != j) {
                    throw new AssertionError("Copied insufficient number of bytes!");
                }
            }
            if (this.last.position < this.first.position) {
                int i4 = (this.fileLength + this.last.position) - 16;
                writeHeader(i3, this.elementCount, this.first.position, i4);
                this.last = new Element(i4, this.last.length);
            } else {
                writeHeader(i3, this.elementCount, this.first.position, this.last.position);
            }
            this.fileLength = i3;
        }
    }

    private void setLength(int i) throws IOException {
        this.raf.setLength((long) i);
        this.raf.getChannel().force(true);
    }

    public synchronized void forEach(ElementReader elementReader) throws IOException {
        int i = this.first.position;
        for (int i2 = 0; i2 < this.elementCount; i2++) {
            Element readElement = readElement(i);
            elementReader.read(new ElementInputStream(readElement), readElement.length);
            i = wrapPosition(readElement.position + 4 + readElement.length);
        }
    }

    /* access modifiers changed from: private */
    public static <T> T nonNull(T t, String str) {
        if (t != null) {
            return t;
        }
        throw new NullPointerException(str);
    }

    public synchronized void remove() throws IOException {
        if (isEmpty()) {
            throw new NoSuchElementException();
        } else if (this.elementCount == 1) {
            clear();
        } else {
            int wrapPosition = wrapPosition(this.first.position + 4 + this.first.length);
            ringRead(wrapPosition, this.buffer, 0, 4);
            int readInt = readInt(this.buffer, 0);
            writeHeader(this.fileLength, this.elementCount - 1, wrapPosition, this.last.position);
            this.elementCount--;
            this.first = new Element(wrapPosition, readInt);
        }
    }

    public synchronized void clear() throws IOException {
        writeHeader(4096, 0, 0, 0);
        this.elementCount = 0;
        this.first = Element.NULL;
        this.last = Element.NULL;
        if (this.fileLength > 4096) {
            setLength(4096);
        }
        this.fileLength = 4096;
    }

    public synchronized void close() throws IOException {
        this.raf.close();
    }

    public boolean hasSpaceFor(int i, int i2) {
        return (usedBytes() + 4) + i <= i2;
    }

    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append('[');
        sb.append("fileLength=");
        sb.append(this.fileLength);
        sb.append(", size=");
        sb.append(this.elementCount);
        sb.append(", first=");
        sb.append(this.first);
        sb.append(", last=");
        sb.append(this.last);
        sb.append(", element lengths=[");
        try {
            forEach(new ElementReader() {
                boolean first = true;

                public void read(InputStream inputStream, int i) throws IOException {
                    if (this.first) {
                        this.first = false;
                    } else {
                        sb.append(", ");
                    }
                    sb.append(i);
                }
            });
        } catch (IOException e) {
            LOGGER.log(Level.WARNING, "read error", e);
        }
        sb.append("]]");
        return sb.toString();
    }
}
