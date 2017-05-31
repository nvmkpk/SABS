package com.getadhell.androidapp.deviceadmin;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.app.enterprise.ApplicationPolicy;
import android.app.enterprise.EnterpriseDeviceManager;
import android.app.enterprise.license.EnterpriseLicenseManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.BuildConfig;
import com.getadhell.androidapp.net.CustomResponse;
import com.getadhell.androidapp.receiver.CustomDeviceAdminReceiver;
import com.getadhell.androidapp.utils.DeviceUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.concurrent.TimeUnit;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DeviceAdminInteractor {
    private static final int RESULT_ENABLE = 42;
    private static DeviceAdminInteractor mInstance = null;
    private final String TAG = DeviceAdminInteractor.class.getCanonicalName();
    private ComponentName componentName;
    private DevicePolicyManager devicePolicyManager;
    private EnterpriseDeviceManager enterpriseDeviceManager;
    private ApplicationPolicy mApplicationPolicy;
    /**
     * Samsung KNOX Standard SDK
     */
    private Context mContext;

    private DeviceAdminInteractor() {
        this.mContext = App.get().getApplicationContext();
        devicePolicyManager =
                (DevicePolicyManager) this.mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this.mContext, CustomDeviceAdminReceiver.class);
        enterpriseDeviceManager = DeviceUtils.getEnterpriseDeviceManager();
        if (isKnoxEnbaled()) {
            mApplicationPolicy = enterpriseDeviceManager.getApplicationPolicy();
        }
    }


    public static DeviceAdminInteractor getInstance() {
        if (mInstance == null) {
            mInstance = getSync();
        }
        return mInstance;
    }

    private static synchronized DeviceAdminInteractor getSync() {
        if (mInstance == null) {
            mInstance = new DeviceAdminInteractor();
        }
        return mInstance;
    }


    /**
     * Check if admin enabled
     *
     * @return void
     */
    public boolean isActiveAdmin() {
        return devicePolicyManager.isAdminActive(componentName);
    }

    /**
     * Force user to enadle administrator
     */
    public void forceEnableAdmin(Context context) {
        Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
        intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, componentName);
        intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Policy provider");
        ((Activity) context).startActivityForResult(intent, RESULT_ENABLE);
    }


    /**
     * Force to activate Samsung KNOX Standard SDK
     */
    public void forceActivateKnox(String knoxKey) throws Exception {
        try {
            EnterpriseLicenseManager.getInstance(mContext)
                    .activateLicense(knoxKey);
        } catch (Exception e) {
            Log.e(TAG, "Failed to activate license", e);
            throw new Exception("Failed to activate license");
        }

    }

    /**
     * Check if KNOX enabled
     */
    public boolean isKnoxEnbaled() {
        return (mContext.checkCallingOrSelfPermission("android.permission.sec.MDM_FIREWALL")
                == PackageManager.PERMISSION_GRANTED)
                && (mContext.checkCallingOrSelfPermission("android.permission.sec.MDM_APP_MGMT")
                == PackageManager.PERMISSION_GRANTED);
    }

    public String getKnoxKey() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        RequestBody formBody = new FormBody.Builder()
                .add("adhellToken", BuildConfig.ADHELL_TOKEN)
                .build();
        Request request = new Request.Builder()
                .url(BuildConfig.ADHELL_KEY_URL)
                .post(formBody)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            if (response.code() != 200) {
                return null;
            }
            String responseBody = response.body().string();
            Gson gson = new Gson();
            CustomResponse customResponse = gson.fromJson(responseBody, CustomResponse.class);
            if (customResponse.data != null) {
                return customResponse.data.toString();
            }
            return null;
        } catch (InterruptedIOException e) {
            Log.e(TAG, "Problem with getting Knox Key from adhell server", e);
            return null;
        } catch (IOException e) {
            Log.e(TAG, "Problem with getting Knox Key from adhell server", e);
            return null;
        } catch (Throwable e) {
            Log.e(TAG, "Problem with getting Knox Key from adhell server", e);
            return null;
        }
    }

    public boolean installApk(String pathToApk) {
        if (mApplicationPolicy == null) {
            Log.i(TAG, "mApplicationPolicy variable is null");
            if (isKnoxEnbaled()) {
                Log.i(TAG, "Getting instance of applicationPolicy");
                mApplicationPolicy = enterpriseDeviceManager.getApplicationPolicy();
            } else {
                Log.w(TAG, "Knox is disabled. No way of getting instance of applicationPolicy");
                return false;
            }
        }
        try {
            File file = new File(pathToApk);
            if (!file.exists()) {
                Log.i(TAG, "apk fail does not exist: " + pathToApk);
                return false;
            }

            boolean result = mApplicationPolicy.installApplication(pathToApk, false);
            Log.i(TAG, "Is Application installed: " + result);
            return result;
        } catch (Throwable e) {
            Log.e(TAG, "Failed to install application", e);
            return false;
        }
    }
}
