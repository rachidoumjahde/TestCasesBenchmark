package com.google.android.gms.internal.auth;

import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.auth.api.credentials.Credential;
import com.google.android.gms.common.api.Status;

public abstract class zzbb extends zze implements zzba {
    public zzbb() {
        super("com.google.android.gms.auth.api.credentials.internal.ICredentialsCallbacks");
    }

    /* access modifiers changed from: protected */
    public final boolean dispatchTransaction(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
        switch (i) {
            case 1:
                zzd((Status) e.a(parcel, Status.CREATOR), (Credential) e.a(parcel, Credential.CREATOR));
                break;
            case 2:
                zze((Status) e.a(parcel, Status.CREATOR));
                break;
            case 3:
                zzd((Status) e.a(parcel, Status.CREATOR), parcel.readString());
                break;
            default:
                return false;
        }
        parcel2.writeNoException();
        return true;
    }
}
