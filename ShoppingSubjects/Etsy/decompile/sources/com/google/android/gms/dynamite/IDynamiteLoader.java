package com.google.android.gms.dynamite;

import android.os.IBinder;
import android.os.IInterface;
import android.os.Parcel;
import android.os.RemoteException;
import com.google.android.gms.dynamic.IObjectWrapper;
import com.google.android.gms.internal.stable.a;
import com.google.android.gms.internal.stable.zza;
import com.google.android.gms.internal.stable.zzb;

public interface IDynamiteLoader extends IInterface {

    public static abstract class Stub extends zzb implements IDynamiteLoader {

        public static class Proxy extends zza implements IDynamiteLoader {
            Proxy(IBinder iBinder) {
                super(iBinder, "com.google.android.gms.dynamite.IDynamiteLoader");
            }

            public IObjectWrapper createModuleContext(IObjectWrapper iObjectWrapper, String str, int i) throws RemoteException {
                Parcel obtainAndWriteInterfaceToken = obtainAndWriteInterfaceToken();
                a.a(obtainAndWriteInterfaceToken, (IInterface) iObjectWrapper);
                obtainAndWriteInterfaceToken.writeString(str);
                obtainAndWriteInterfaceToken.writeInt(i);
                Parcel transactAndReadException = transactAndReadException(2, obtainAndWriteInterfaceToken);
                IObjectWrapper asInterface = com.google.android.gms.dynamic.IObjectWrapper.Stub.asInterface(transactAndReadException.readStrongBinder());
                transactAndReadException.recycle();
                return asInterface;
            }

            public int getModuleVersion(IObjectWrapper iObjectWrapper, String str) throws RemoteException {
                Parcel obtainAndWriteInterfaceToken = obtainAndWriteInterfaceToken();
                a.a(obtainAndWriteInterfaceToken, (IInterface) iObjectWrapper);
                obtainAndWriteInterfaceToken.writeString(str);
                Parcel transactAndReadException = transactAndReadException(1, obtainAndWriteInterfaceToken);
                int readInt = transactAndReadException.readInt();
                transactAndReadException.recycle();
                return readInt;
            }

            public int getModuleVersion2(IObjectWrapper iObjectWrapper, String str, boolean z) throws RemoteException {
                Parcel obtainAndWriteInterfaceToken = obtainAndWriteInterfaceToken();
                a.a(obtainAndWriteInterfaceToken, (IInterface) iObjectWrapper);
                obtainAndWriteInterfaceToken.writeString(str);
                a.a(obtainAndWriteInterfaceToken, z);
                Parcel transactAndReadException = transactAndReadException(3, obtainAndWriteInterfaceToken);
                int readInt = transactAndReadException.readInt();
                transactAndReadException.recycle();
                return readInt;
            }
        }

        public Stub() {
            super("com.google.android.gms.dynamite.IDynamiteLoader");
        }

        public static IDynamiteLoader asInterface(IBinder iBinder) {
            if (iBinder == null) {
                return null;
            }
            IInterface queryLocalInterface = iBinder.queryLocalInterface("com.google.android.gms.dynamite.IDynamiteLoader");
            return queryLocalInterface instanceof IDynamiteLoader ? (IDynamiteLoader) queryLocalInterface : new Proxy(iBinder);
        }

        /* access modifiers changed from: protected */
        public boolean dispatchTransaction(int i, Parcel parcel, Parcel parcel2, int i2) throws RemoteException {
            int i3;
            switch (i) {
                case 1:
                    i3 = getModuleVersion(com.google.android.gms.dynamic.IObjectWrapper.Stub.asInterface(parcel.readStrongBinder()), parcel.readString());
                    break;
                case 2:
                    IObjectWrapper createModuleContext = createModuleContext(com.google.android.gms.dynamic.IObjectWrapper.Stub.asInterface(parcel.readStrongBinder()), parcel.readString(), parcel.readInt());
                    parcel2.writeNoException();
                    a.a(parcel2, (IInterface) createModuleContext);
                    break;
                case 3:
                    i3 = getModuleVersion2(com.google.android.gms.dynamic.IObjectWrapper.Stub.asInterface(parcel.readStrongBinder()), parcel.readString(), a.a(parcel));
                    break;
                default:
                    return false;
            }
            parcel2.writeNoException();
            parcel2.writeInt(i3);
            return true;
        }
    }

    IObjectWrapper createModuleContext(IObjectWrapper iObjectWrapper, String str, int i) throws RemoteException;

    int getModuleVersion(IObjectWrapper iObjectWrapper, String str) throws RemoteException;

    int getModuleVersion2(IObjectWrapper iObjectWrapper, String str, boolean z) throws RemoteException;
}