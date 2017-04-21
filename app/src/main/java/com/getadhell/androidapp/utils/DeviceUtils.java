package com.getadhell.androidapp.utils;

import android.app.enterprise.EnterpriseDeviceManager;
import android.app.enterprise.license.EnterpriseLicenseManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import com.getadhell.androidapp.blocker.ContentBlocker;
import com.getadhell.androidapp.blocker.ContentBlocker20;
import com.getadhell.androidapp.blocker.ContentBlocker56;
import com.getadhell.androidapp.blocker.ContentBlocker57;

public class DeviceUtils {
    private static final String LOG_TAG = DeviceUtils.class.getCanonicalName();

    private static boolean isSamsung() {
        Log.i(LOG_TAG, "Device manufacturer: " + Build.MANUFACTURER);
        return Build.MANUFACTURER.equals("samsung");
    }

    private static boolean isKnoxSupported(Context context) {
        Log.d(LOG_TAG, "Entering isKnoxSupported()");
        try {
            EnterpriseLicenseManager.getInstance(context);
        } catch (Throwable ex) {
            Log.e(LOG_TAG, "Seems KNOX not supported", ex);
            return false;
        }
        Log.i(LOG_TAG, "KNOX exist");
        return true;
    }

    private static boolean isKnoxVersionSupported(Context context) {
        Log.d(LOG_TAG, "Entering isKnoxVersionSupported() method");
        try {
            EnterpriseDeviceManager edm = (EnterpriseDeviceManager)
                    context.getSystemService(EnterpriseDeviceManager.ENTERPRISE_POLICY_SERVICE);
            switch (edm.getEnterpriseSdkVer()) {
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
                    return true;
                default:
                    return true;
            }
        } catch (Throwable ex) {
            Log.e(LOG_TAG, "Knox version not supported", ex);
            return false;
        }
    }

    public static boolean isContentBlockerSupported(Context context) {
        return (isSamsung() && isKnoxSupported(context) && isKnoxVersionSupported(context));
    }

    public static ContentBlocker getContentBlocker(Context context) {
        Log.d(LOG_TAG, "Entering contentBlocker() method");
        EnterpriseDeviceManager edm = (EnterpriseDeviceManager)
                context.getSystemService(EnterpriseDeviceManager.ENTERPRISE_POLICY_SERVICE);
        Log.w(LOG_TAG, "EDM version: " + edm.getEnterpriseSdkVer());
        try {
            switch (edm.getEnterpriseSdkVer()) {
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
                    return new ContentBlocker20(context, 1625); // works
                case ENTERPRISE_SDK_VERSION_5_4:
                    return new ContentBlocker20(context, 1625);
                case ENTERPRISE_SDK_VERSION_5_4_1:
                    return new ContentBlocker20(context, 300); // works
                case ENTERPRISE_SDK_VERSION_5_5:
                    return new ContentBlocker20(context, 300);
                case ENTERPRISE_SDK_VERSION_5_5_1:
                    return new ContentBlocker20(context, 300);
                case ENTERPRISE_SDK_VERSION_5_6:
                    return new ContentBlocker56(context); // works
                case ENTERPRISE_SDK_VERSION_5_7:
                    return new ContentBlocker57(context);
                case ENTERPRISE_SDK_VERSION_5_7_1:
                    return new ContentBlocker57(context);
                default:
                    return new ContentBlocker56(context);
            }
        } catch (Throwable t) {
            Log.e(LOG_TAG, "Failed to get ContentBlocker", t);
            return null;
        }
    }
}
