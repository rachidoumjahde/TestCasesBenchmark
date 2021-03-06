package com.google.android.gms.internal.wallet;

import android.os.Bundle;
import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.wallet.FullWallet;
import com.google.android.gms.wallet.MaskedWallet;
import com.google.android.gms.wallet.PaymentData;
import com.google.android.gms.wallet.zzar;

public interface zzu extends IInterface {
    void zza(int i, Bundle bundle) throws RemoteException;

    void zza(int i, FullWallet fullWallet, Bundle bundle) throws RemoteException;

    void zza(int i, MaskedWallet maskedWallet, Bundle bundle) throws RemoteException;

    void zza(int i, boolean z, Bundle bundle) throws RemoteException;

    void zza(Status status, Bundle bundle) throws RemoteException;

    void zza(Status status, zzf zzf, Bundle bundle) throws RemoteException;

    void zza(Status status, zzh zzh, Bundle bundle) throws RemoteException;

    void zza(Status status, zzj zzj, Bundle bundle) throws RemoteException;

    void zza(Status status, PaymentData paymentData, Bundle bundle) throws RemoteException;

    void zza(Status status, zzar zzar, Bundle bundle) throws RemoteException;

    void zza(Status status, boolean z, Bundle bundle) throws RemoteException;

    void zzb(int i, boolean z, Bundle bundle) throws RemoteException;

    void zzb(Status status, Bundle bundle) throws RemoteException;

    void zzc(Status status, Bundle bundle) throws RemoteException;
}
