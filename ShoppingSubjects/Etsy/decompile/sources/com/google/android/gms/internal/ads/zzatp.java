package com.google.android.gms.internal.ads;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.dynamic.IObjectWrapper.Stub;

public final class zzatp extends zzej implements zzatn {
    zzatp(IBinder iBinder) {
        super(iBinder, "com.google.android.gms.ads.omid.IOmid");
    }

    public final String getVersion() throws RemoteException {
        Parcel transactAndReadException = transactAndReadException(6, obtainAndWriteInterfaceToken());
        String readString = transactAndReadException.readString();
        transactAndReadException.recycle();
        return readString;
    }

    public final IObjectWrapper zza(String str, IObjectWrapper iObjectWrapper, String str2, String str3, String str4) throws RemoteException {
        Parcel obtainAndWriteInterfaceToken = obtainAndWriteInterfaceToken();
        obtainAndWriteInterfaceToken.writeString(str);
        aej.a(obtainAndWriteInterfaceToken, (IInterface) iObjectWrapper);
        obtainAndWriteInterfaceToken.writeString(str2);
        obtainAndWriteInterfaceToken.writeString(str3);
        obtainAndWriteInterfaceToken.writeString(str4);
        Parcel transactAndReadException = transactAndReadException(3, obtainAndWriteInterfaceToken);
        IObjectWrapper asInterface = Stub.asInterface(transactAndReadException.readStrongBinder());
        transactAndReadException.recycle();
        return asInterface;
    }

    public final void zza(IObjectWrapper iObjectWrapper, IObjectWrapper iObjectWrapper2) throws RemoteException {
        Parcel obtainAndWriteInterfaceToken = obtainAndWriteInterfaceToken();
        aej.a(obtainAndWriteInterfaceToken, (IInterface) iObjectWrapper);
        aej.a(obtainAndWriteInterfaceToken, (IInterface) iObjectWrapper2);
        transactAndReadExceptionReturnVoid(5, obtainAndWriteInterfaceToken);
    }

    public final void zzm(IObjectWrapper iObjectWrapper) throws RemoteException {
        Parcel obtainAndWriteInterfaceToken = obtainAndWriteInterfaceToken();
        aej.a(obtainAndWriteInterfaceToken, (IInterface) iObjectWrapper);
        transactAndReadExceptionReturnVoid(4, obtainAndWriteInterfaceToken);
    }

    public final void zzn(IObjectWrapper iObjectWrapper) throws RemoteException {
        Parcel obtainAndWriteInterfaceToken = obtainAndWriteInterfaceToken();
        aej.a(obtainAndWriteInterfaceToken, (IInterface) iObjectWrapper);
        transactAndReadExceptionReturnVoid(7, obtainAndWriteInterfaceToken);
    }

    public final boolean zzy(IObjectWrapper iObjectWrapper) throws RemoteException {
        Parcel obtainAndWriteInterfaceToken = obtainAndWriteInterfaceToken();
        aej.a(obtainAndWriteInterfaceToken, (IInterface) iObjectWrapper);
        Parcel transactAndReadException = transactAndReadException(2, obtainAndWriteInterfaceToken);
        boolean a = aej.a(transactAndReadException);
        transactAndReadException.recycle();
        return a;
    }
}
