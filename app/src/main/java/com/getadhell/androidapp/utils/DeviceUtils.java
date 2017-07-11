package com.getadhell.androidapp.utils;

import android.app.enterprise.EnterpriseDeviceManager;
import android.app.enterprise.license.EnterpriseLicenseManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.blocker.ContentBlocker;
import com.getadhell.androidapp.blocker.ContentBlocker20;
import com.getadhell.androidapp.blocker.ContentBlocker56;
import com.getadhell.androidapp.blocker.ContentBlocker57;

public class DeviceUtils {
    private static final String TAG = DeviceUtils.class.getCanonicalName();

    public static boolean isSamsung() {
        Log.i(TAG, "Device manufacturer: " + Build.MANUFACTURER);
        return Build.MANUFACTURER.equals("samsung");
    }

    public static boolean isKnoxSupported() {
        Log.d(TAG, "Entering isKnoxSupported()");
        try {
            EnterpriseLicenseManager.getInstance(App.get().getApplicationContext());
        } catch (Throwable ex) {
            Log.e(TAG, "Seems KNOX not supported", ex);
            return false;
        }
        Log.i(TAG, "KNOX exist");
        return true;
    }

    private static boolean isKnoxVersionSupported() {
        Log.d(TAG, "Entering isKnoxVersionSupported() method");
        try {
            switch (getEnterpriseDeviceManager().getEnterpriseSdkVer()) {
                case ENTERPRISE_SDK_VERSION_NONE:
                    return false;
                case ENTERPRISE_SDK_VERSION_2:
                case ENTERPRISE_SDK_VERSION_2_1:
                case ENTERPRISE_SDK_VERSION_2_2:
                case ENTERPRISE_SDK_VERSION_3:
                case ENTERPRISE_SDK_VERSION_4:
                case ENTERPRISE_SDK_VERSION_4_0_1:
                case ENTERPRISE_SDK_VERSION_4_1:
                case ENTERPRISE_SDK_VERSION_5:
                case ENTERPRISE_SDK_VERSION_5_1:
                case ENTERPRISE_SDK_VERSION_5_2:
                case ENTERPRISE_SDK_VERSION_5_3:
                case ENTERPRISE_SDK_VERSION_5_4:
                case ENTERPRISE_SDK_VERSION_5_4_1:
                case ENTERPRISE_SDK_VERSION_5_5:
                case ENTERPRISE_SDK_VERSION_5_5_1:
                case ENTERPRISE_SDK_VERSION_5_6:
                case ENTERPRISE_SDK_VERSION_5_7:
                case ENTERPRISE_SDK_VERSION_5_7_1:
                case ENTERPRISE_SDK_VERSION_5_8:
                    return true;
                default:
                    return true;
            }
        } catch (Throwable ex) {
            Log.e(TAG, "Knox version not supported", ex);
            return false;
        }
    }

    public static boolean isContentBlockerSupported() {
        return (isSamsung() && isKnoxSupported() && isKnoxVersionSupported());
    }

    public static ContentBlocker getContentBlocker() {
        Log.d(TAG, "Entering contentBlocker() method");
        try {
            switch (getEnterpriseDeviceManager().getEnterpriseSdkVer()) {
                case ENTERPRISE_SDK_VERSION_NONE:
                    return null;
                case ENTERPRISE_SDK_VERSION_2:
                case ENTERPRISE_SDK_VERSION_2_1:
                case ENTERPRISE_SDK_VERSION_2_2:
                case ENTERPRISE_SDK_VERSION_3:
                case ENTERPRISE_SDK_VERSION_4:
                case ENTERPRISE_SDK_VERSION_4_0_1:
                case ENTERPRISE_SDK_VERSION_4_1:
                case ENTERPRISE_SDK_VERSION_5:
                case ENTERPRISE_SDK_VERSION_5_1:
                case ENTERPRISE_SDK_VERSION_5_2:
                case ENTERPRISE_SDK_VERSION_5_3:
                case ENTERPRISE_SDK_VERSION_5_4:
                    ContentBlocker20.getInstance().setUrlBlockLimit(625);
                    return ContentBlocker20.getInstance();
                case ENTERPRISE_SDK_VERSION_5_4_1:
                case ENTERPRISE_SDK_VERSION_5_5:
                case ENTERPRISE_SDK_VERSION_5_5_1:
                    ContentBlocker20.getInstance().setUrlBlockLimit(625);
                    return ContentBlocker20.getInstance();
                case ENTERPRISE_SDK_VERSION_5_6:
                    return ContentBlocker56.getInstance();
                case ENTERPRISE_SDK_VERSION_5_7:
                    return ContentBlocker57.getInstance();
                case ENTERPRISE_SDK_VERSION_5_7_1:
                    return ContentBlocker57.getInstance();
                case ENTERPRISE_SDK_VERSION_5_8:
                    return ContentBlocker57.getInstance();
                default:
                    return ContentBlocker57.getInstance();
            }
        } catch (Throwable t) {
            Log.e(TAG, "Failed to getAll ContentBlocker", t);
            return null;
        }
    }

    public static EnterpriseDeviceManager getEnterpriseDeviceManager() {
        Context context = App.get().getApplicationContext();
        EnterpriseDeviceManager edm = (EnterpriseDeviceManager)
                context.getSystemService(EnterpriseDeviceManager.ENTERPRISE_POLICY_SERVICE);
        Log.w(TAG, "EDM version: " + edm.getEnterpriseSdkVer());
        return edm;
    }
}
