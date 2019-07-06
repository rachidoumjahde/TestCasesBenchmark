package com.bumptech.glide.load.b;

import android.net.Uri;
import com.etsy.android.lib.models.ResponseConstants;

/* compiled from: AssetUriParser */
final class a {
    private static final int a = "file:///android_asset/".length();

    public static boolean a(Uri uri) {
        return ResponseConstants.FILE.equals(uri.getScheme()) && !uri.getPathSegments().isEmpty() && "android_asset".equals(uri.getPathSegments().get(0));
    }

    public static String b(Uri uri) {
        return uri.toString().substring(a);
    }
}