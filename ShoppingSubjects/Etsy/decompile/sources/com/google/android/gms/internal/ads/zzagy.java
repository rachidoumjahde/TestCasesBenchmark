package com.google.android.gms.internal.ads;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;

public final class zzagy extends zzej implements zzagx {
    zzagy(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.ads.internal.reward.client.IRewardedAdSkuListener");
    }

    public final void zza(zzagu zzagu, String str) throws RemoteException {
        Parcel obtainAndWriteInterfaceToken = obtainAndWriteInterfaceToken();
        aej.a(obtainAndWriteInterfaceToken, (IInterface) zzagu);
        obtainAndWriteInterfaceToken.writeString(str);
        transactAndReadExceptionReturnVoid(1, obtainAndWriteInterfaceToken);
    }
}
