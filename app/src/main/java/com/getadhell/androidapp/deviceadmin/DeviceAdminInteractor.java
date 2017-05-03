package com.getadhell.androidapp.deviceadmin;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.app.enterprise.license.EnterpriseLicenseManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.BuildConfig;
import com.getadhell.androidapp.net.CustomResponse;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class DeviceAdminInteractor {
    private static final int RESULT_ENABLE = 42;
    private final String TAG = DeviceAdminInteractor.class.getCanonicalName();
    private ComponentName componentName;
    private DevicePolicyManager devicePolicyManager;
    /**
     * Samsung KNOX Standard SDK
     */
    private Context mContext;

    public DeviceAdminInteractor() {
        this.mContext = App.get().getApplicationContext();
        devicePolicyManager =
                (DevicePolicyManager) this.mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
        componentName = new ComponentName(this.mContext, CustomDeviceAdminReceiver.class);
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
    public void forceActivateKnox() throws Exception {
        try {
            String knoxKey = getKnoxKey();
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
        return mContext.checkCallingOrSelfPermission("android.permission.sec.MDM_FIREWALL")
                == PackageManager.PERMISSION_GRANTED;
    }

    public String getKnoxKey() {
        OkHttpClient okHttpClient = new OkHttpClient();
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
        } catch (IOException e) {
            Log.e(TAG, "Problem with getting Knox Key from adhell server", e);
            return null;
        }
//        AppConfig appConfig = AppConfig.loadConfigFromAsset(mContext);
    }
}
