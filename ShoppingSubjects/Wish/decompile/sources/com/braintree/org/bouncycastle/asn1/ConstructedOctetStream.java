package com.braintree.org.bouncycastle.asn1;

import java.io.IOException;
import java.io.InputStream;

class ConstructedOctetStream extends InputStream {
    private InputStream _currentStream;
    private boolean _first = true;
    private final ASN1StreamParser _parser;

    ConstructedOctetStream(ASN1StreamParser aSN1StreamParser) {
        this._parser = aSN1StreamParser;
    }

    public int read(byte[] bArr, int i, int i2) throws IOException {
        int i3 = 0;
        if (this._currentStream == null) {
            if (!this._first) {
                return -1;
            }
            ASN1OctetStringParser aSN1OctetStringParser = (ASN1OctetStringParser) this._parser.readObject();
            if (aSN1OctetStringParser == null) {
                return -1;
            }
            this._first = false;
            this._currentStream = aSN1OctetStringParser.getOctetStream();
        }
        while (true) {
            int read = this._currentStream.read(bArr, i + i3, i2 - i3);
            if (read >= 0) {
                i3 += read;
                if (i3 == i2) {
                    return i3;
                }
            } else {
                ASN1OctetStringParser aSN1OctetStringParser2 = (ASN1OctetStringParser) this._parser.readObject();
                if (aSN1OctetStringParser2 == null) {
                    this._currentStream = null;
                    if (i3 < 1) {
                        i3 = -1;
                    }
                    return i3;
                }
                this._currentStream = aSN1OctetStringParser2.getOctetStream();
            }
        }
    }

    public int read() throws IOException {
        if (this._currentStream == null) {
            if (!this._first) {
                return -1;
            }
            ASN1OctetStringParser aSN1OctetStringParser = (ASN1OctetStringParser) this._parser.readObject();
            if (aSN1OctetStringParser == null) {
                return -1;
            }
            this._first = false;
            this._currentStream = aSN1OctetStringParser.getOctetStream();
        }
        while (true) {
            int read = this._currentStream.read();
            if (read >= 0) {
                return read;
            }
            ASN1OctetStringParser aSN1OctetStringParser2 = (ASN1OctetStringParser) this._parser.readObject();
            if (aSN1OctetStringParser2 == null) {
                this._currentStream = null;
                return -1;
            }
            this._currentStream = aSN1OctetStringParser2.getOctetStream();
        }
    }
}