package com.lody.virtual.helper.compat;

import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Parcel;

import mirror.android.os.BaseBundle;
import mirror.android.os.BundleICS;

/**
 * @author Lody
 */
public class BundleCompat {

    private static IBundleCompat BC;

    static {
        BC = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2 ?
                new IBundleCompat() {
                    @Override
                    public IBinder getBinder(Bundle bundle, String key) {
                        return bundle.getBinder(key);
                    }

                    @Override
                    public void putBinder(Bundle bundle, String key, IBinder value) {
                        bundle.putBinder(key, value);
                    }
                } :
                new IBundleCompat() {
                    @Override
                    public IBinder getBinder(Bundle bundle, String key) {
                        return mirror.android.os.Bundle.getIBinder.call(bundle, key);
                    }

                    @Override
                    public void putBinder(Bundle bundle, String key, IBinder value) {
                        mirror.android.os.Bundle.putIBinder.call(bundle, key, value);
                    }
                };
    }

    interface IBundleCompat {
        IBinder getBinder(Bundle bundle, String key);
        void putBinder(Bundle bundle, String key, IBinder value);
    }

    private BundleCompat() {
    }

    public static IBinder getBinder(Bundle bundle, String key) {
        return BC.getBinder(bundle, key);
    }

    public static void putBinder(Bundle bundle, String key, IBinder value) {
        BC.putBinder(bundle, key, value);
    }

    public static void clearParcelledData(Bundle bundle) {
        Parcel obtain = Parcel.obtain();
        obtain.writeInt(0);
        obtain.setDataPosition(0);
        Parcel parcel;
        if (BaseBundle.TYPE != null) {
            parcel = BaseBundle.mParcelledData.get(bundle);
            if (parcel != null) {
                parcel.recycle();
            }
            BaseBundle.mParcelledData.set(bundle, obtain);
        } else if (BundleICS.TYPE != null) {
            parcel = BundleICS.mParcelledData.get(bundle);
            if (parcel != null) {
                parcel.recycle();
            }
            BundleICS.mParcelledData.set(bundle, obtain);
        }
    }
}
