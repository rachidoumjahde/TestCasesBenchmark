package com.bumptech.glide.load.resource.gif;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import com.bumptech.glide.b.a.C0008a;
import com.bumptech.glide.load.engine.bitmap_recycle.c;

/* compiled from: GifBitmapProvider */
class a implements C0008a {
    private final c a;

    public a(c cVar) {
        this.a = cVar;
    }

    public Bitmap a(int i, int i2, Config config) {
        return this.a.b(i, i2, config);
    }

    public void a(Bitmap bitmap) {
        if (!this.a.a(bitmap)) {
            bitmap.recycle();
        }
    }
}
