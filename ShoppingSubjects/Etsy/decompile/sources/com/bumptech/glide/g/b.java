package com.bumptech.glide.g;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/* compiled from: ContentLengthInputStream */
public final class b extends FilterInputStream {
    private final long a;
    private int b;

    public static InputStream a(InputStream inputStream, long j) {
        return new b(inputStream, j);
    }

    b(InputStream inputStream, long j) {
        super(inputStream);
        this.a = j;
    }

    public synchronized int available() throws IOException {
        return (int) Math.max(this.a - ((long) this.b), (long) this.in.available());
    }

    public synchronized int read() throws IOException {
        return a(super.read());
    }

    public int read(byte[] bArr) throws IOException {
        return read(bArr, 0, bArr.length);
    }

    public synchronized int read(byte[] bArr, int i, int i2) throws IOException {
        return a(super.read(bArr, i, i2));
    }

    private int a(int i) throws IOException {
        if (i >= 0) {
            this.b += i;
        } else if (this.a - ((long) this.b) > 0) {
            StringBuilder sb = new StringBuilder();
            sb.append("Failed to read all expected data, expected: ");
            sb.append(this.a);
            sb.append(", but read: ");
            sb.append(this.b);
            throw new IOException(sb.toString());
        }
        return i;
    }
}