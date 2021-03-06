package com.braintree.org.bouncycastle.asn1;

import java.io.IOException;

public class BEROutputStream extends DEROutputStream {
    public void writeObject(Object obj) throws IOException {
        if (obj == null) {
            writeNull();
        } else if (obj instanceof DERObject) {
            ((DERObject) obj).encode(this);
        } else if (obj instanceof DEREncodable) {
            ((DEREncodable) obj).getDERObject().encode(this);
        } else {
            throw new IOException("object not BEREncodable");
        }
    }
}
