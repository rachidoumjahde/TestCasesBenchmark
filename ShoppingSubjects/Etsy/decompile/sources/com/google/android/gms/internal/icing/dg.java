package com.google.android.gms.internal.icing;

import java.io.IOException;
import java.nio.BufferOverflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ReadOnlyBufferException;

public final class dg {
    private final ByteBuffer a;

    private dg(ByteBuffer byteBuffer) {
        this.a = byteBuffer;
        this.a.order(ByteOrder.LITTLE_ENDIAN);
    }

    private dg(byte[] bArr, int i, int i2) {
        this(ByteBuffer.wrap(bArr, i, i2));
    }

    public static int a(int i) {
        return c(i << 3);
    }

    private static int a(CharSequence charSequence) {
        int length = charSequence.length();
        int i = 0;
        int i2 = 0;
        while (i2 < length && charSequence.charAt(i2) < 128) {
            i2++;
        }
        int i3 = length;
        while (true) {
            if (i2 >= length) {
                break;
            }
            char charAt = charSequence.charAt(i2);
            if (charAt < 2048) {
                i3 += (127 - charAt) >>> 31;
                i2++;
            } else {
                int length2 = charSequence.length();
                while (i2 < length2) {
                    char charAt2 = charSequence.charAt(i2);
                    if (charAt2 < 2048) {
                        i += (127 - charAt2) >>> 31;
                    } else {
                        i += 2;
                        if (55296 <= charAt2 && charAt2 <= 57343) {
                            if (Character.codePointAt(charSequence, i2) < 65536) {
                                StringBuilder sb = new StringBuilder(39);
                                sb.append("Unpaired surrogate at index ");
                                sb.append(i2);
                                throw new IllegalArgumentException(sb.toString());
                            }
                            i2++;
                        }
                    }
                    i2++;
                }
                i3 += i;
            }
        }
        if (i3 >= length) {
            return i3;
        }
        long j = ((long) i3) + 4294967296L;
        StringBuilder sb2 = new StringBuilder(54);
        sb2.append("UTF-8 length does not fit in int: ");
        sb2.append(j);
        throw new IllegalArgumentException(sb2.toString());
    }

    public static dg a(byte[] bArr) {
        return a(bArr, 0, bArr.length);
    }

    public static dg a(byte[] bArr, int i, int i2) {
        return new dg(bArr, 0, i2);
    }

    private static void a(CharSequence charSequence, ByteBuffer byteBuffer) {
        int i;
        int i2;
        int i3;
        CharSequence charSequence2 = charSequence;
        ByteBuffer byteBuffer2 = byteBuffer;
        if (byteBuffer.isReadOnly()) {
            throw new ReadOnlyBufferException();
        }
        char c = 2048;
        if (byteBuffer.hasArray()) {
            try {
                byte[] array = byteBuffer.array();
                int arrayOffset = byteBuffer.arrayOffset() + byteBuffer.position();
                int remaining = byteBuffer.remaining();
                int length = charSequence.length();
                int i4 = remaining + arrayOffset;
                int i5 = 0;
                while (i5 < length) {
                    int i6 = i5 + arrayOffset;
                    if (i6 >= i4) {
                        break;
                    }
                    char charAt = charSequence2.charAt(i5);
                    if (charAt >= 128) {
                        break;
                    }
                    array[i6] = (byte) charAt;
                    i5++;
                }
                if (i5 == length) {
                    i = arrayOffset + length;
                } else {
                    i = arrayOffset + i5;
                    while (i5 < length) {
                        char charAt2 = charSequence2.charAt(i5);
                        if (charAt2 >= 128 || i >= i4) {
                            if (charAt2 < c && i <= i4 - 2) {
                                int i7 = i + 1;
                                array[i] = (byte) (960 | (charAt2 >>> 6));
                                i2 = i7 + 1;
                                array[i7] = (byte) ((charAt2 & '?') | 128);
                            } else if ((charAt2 < 55296 || 57343 < charAt2) && i <= i4 - 3) {
                                int i8 = i + 1;
                                array[i] = (byte) (480 | (charAt2 >>> 12));
                                int i9 = i8 + 1;
                                array[i8] = (byte) (((charAt2 >>> 6) & 63) | 128);
                                i3 = i9 + 1;
                                array[i9] = (byte) ((charAt2 & '?') | 128);
                            } else if (i <= i4 - 4) {
                                int i10 = i5 + 1;
                                if (i10 != charSequence.length()) {
                                    char charAt3 = charSequence2.charAt(i10);
                                    if (!Character.isSurrogatePair(charAt2, charAt3)) {
                                        i5 = i10;
                                    } else {
                                        int codePoint = Character.toCodePoint(charAt2, charAt3);
                                        int i11 = i + 1;
                                        array[i] = (byte) (240 | (codePoint >>> 18));
                                        int i12 = i11 + 1;
                                        array[i11] = (byte) (((codePoint >>> 12) & 63) | 128);
                                        int i13 = i12 + 1;
                                        array[i12] = (byte) (((codePoint >>> 6) & 63) | 128);
                                        i2 = i13 + 1;
                                        array[i13] = (byte) ((codePoint & 63) | 128);
                                        i5 = i10;
                                    }
                                }
                                int i14 = i5 - 1;
                                StringBuilder sb = new StringBuilder(39);
                                sb.append("Unpaired surrogate at index ");
                                sb.append(i14);
                                throw new IllegalArgumentException(sb.toString());
                            } else {
                                StringBuilder sb2 = new StringBuilder(37);
                                sb2.append("Failed writing ");
                                sb2.append(charAt2);
                                sb2.append(" at index ");
                                sb2.append(i);
                                throw new ArrayIndexOutOfBoundsException(sb2.toString());
                            }
                            i = i2;
                            i5++;
                            c = 2048;
                        } else {
                            i3 = i + 1;
                            array[i] = (byte) charAt2;
                        }
                        i = i3;
                        i5++;
                        c = 2048;
                    }
                }
                byteBuffer2.position(i - byteBuffer.arrayOffset());
            } catch (ArrayIndexOutOfBoundsException e) {
                ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException = e;
                BufferOverflowException bufferOverflowException = new BufferOverflowException();
                bufferOverflowException.initCause(arrayIndexOutOfBoundsException);
                throw bufferOverflowException;
            }
        } else {
            int length2 = charSequence.length();
            int i15 = 0;
            while (i15 < length2) {
                char charAt4 = charSequence2.charAt(i15);
                if (charAt4 < 128) {
                    byteBuffer2.put((byte) charAt4);
                } else if (charAt4 < 2048) {
                    byteBuffer2.put((byte) ((charAt4 >>> 6) | 960));
                    byteBuffer2.put((byte) ((charAt4 & '?') | 128));
                } else if (charAt4 < 55296 || 57343 < charAt4) {
                    byteBuffer2.put((byte) ((charAt4 >>> 12) | 480));
                    byteBuffer2.put((byte) (((charAt4 >>> 6) & 63) | 128));
                    byteBuffer2.put((byte) ((charAt4 & '?') | 128));
                    i15++;
                } else {
                    int i16 = i15 + 1;
                    if (i16 != charSequence.length()) {
                        char charAt5 = charSequence2.charAt(i16);
                        if (!Character.isSurrogatePair(charAt4, charAt5)) {
                            i15 = i16;
                        } else {
                            int codePoint2 = Character.toCodePoint(charAt4, charAt5);
                            byteBuffer2.put((byte) ((codePoint2 >>> 18) | 240));
                            byteBuffer2.put((byte) (((codePoint2 >>> 12) & 63) | 128));
                            byteBuffer2.put((byte) (((codePoint2 >>> 6) & 63) | 128));
                            byteBuffer2.put((byte) ((codePoint2 & 63) | 128));
                            i15 = i16;
                            i15++;
                        }
                    }
                    int i17 = i15 - 1;
                    StringBuilder sb3 = new StringBuilder(39);
                    sb3.append("Unpaired surrogate at index ");
                    sb3.append(i17);
                    throw new IllegalArgumentException(sb3.toString());
                }
                i15++;
            }
        }
    }

    public static int b(int i, dm dmVar) {
        int a2 = a(i);
        int d = dmVar.d();
        return a2 + c(d) + d;
    }

    public static int b(int i, String str) {
        int a2 = a(i);
        int a3 = a((CharSequence) str);
        return a2 + c(a3) + a3;
    }

    public static int c(int i) {
        if ((i & -128) == 0) {
            return 1;
        }
        if ((i & -16384) == 0) {
            return 2;
        }
        if ((-2097152 & i) == 0) {
            return 3;
        }
        return (i & -268435456) == 0 ? 4 : 5;
    }

    private final void d(int i) throws IOException {
        byte b = (byte) i;
        if (!this.a.hasRemaining()) {
            throw new zzft(this.a.position(), this.a.limit());
        }
        this.a.put(b);
    }

    public final void a() {
        if (this.a.remaining() != 0) {
            throw new IllegalStateException(String.format("Did not write as much data as expected, %s bytes remaining.", new Object[]{Integer.valueOf(this.a.remaining())}));
        }
    }

    public final void a(int i, int i2) throws IOException {
        b((i << 3) | i2);
    }

    public final void a(int i, dm dmVar) throws IOException {
        a(i, 2);
        if (dmVar.e < 0) {
            dmVar.d();
        }
        b(dmVar.e);
        dmVar.a(this);
    }

    public final void a(int i, String str) throws IOException {
        a(i, 2);
        try {
            int c = c(str.length());
            if (c == c(str.length() * 3)) {
                int position = this.a.position();
                if (this.a.remaining() < c) {
                    throw new zzft(position + c, this.a.limit());
                }
                this.a.position(position + c);
                a((CharSequence) str, this.a);
                int position2 = this.a.position();
                this.a.position(position);
                b((position2 - position) - c);
                this.a.position(position2);
                return;
            }
            b(a((CharSequence) str));
            a((CharSequence) str, this.a);
        } catch (BufferOverflowException e) {
            zzft zzft = new zzft(this.a.position(), this.a.limit());
            zzft.initCause(e);
            throw zzft;
        }
    }

    public final void a(long j) throws IOException {
        while ((j & -128) != 0) {
            d((((int) j) & 127) | 128);
            j >>>= 7;
        }
        d((int) j);
    }

    public final void b(int i) throws IOException {
        while ((i & -128) != 0) {
            d((i & 127) | 128);
            i >>>= 7;
        }
        d(i);
    }
}