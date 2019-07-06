package com.google.android.gms.maps.internal;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.internal.maps.a;
import com.google.android.gms.internal.maps.zzb;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;

public abstract class zzbm extends zzb implements zzbl {
    public zzbm() {
        super("com.google.android.gms.maps.internal.IOnStreetViewPanoramaClickListener");
    }

    /* access modifiers changed from: protected */
    public final boolean dispatchTransaction(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        if (i != 1) {
            return false;
        }
        onStreetViewPanoramaClick((StreetViewPanoramaOrientation) a.a(parcel, StreetViewPanoramaOrientation.CREATOR));
        parcel2.writeNoException();
        return true;
    }
}