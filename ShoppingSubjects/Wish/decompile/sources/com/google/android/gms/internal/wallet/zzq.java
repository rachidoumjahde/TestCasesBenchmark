package com.google.android.gms.internal.wallet;

import android.os.Bundle;
import android.os.IInterface;
import android.os.RemoteException;
import com.google.android.gms.wallet.FullWalletRequest;
import com.google.android.gms.wallet.IsReadyToPayRequest;
import com.google.android.gms.wallet.MaskedWalletRequest;
import com.google.android.gms.wallet.PaymentDataRequest;

public interface zzq extends IInterface {
    void zza(FullWalletRequest fullWalletRequest, Bundle bundle, zzu zzu) throws RemoteException;

    void zza(IsReadyToPayRequest isReadyToPayRequest, Bundle bundle, zzu zzu) throws RemoteException;

    void zza(MaskedWalletRequest maskedWalletRequest, Bundle bundle, zzu zzu) throws RemoteException;

    void zza(PaymentDataRequest paymentDataRequest, Bundle bundle, zzu zzu) throws RemoteException;

    void zza(String str, String str2, Bundle bundle, zzu zzu) throws RemoteException;
}
