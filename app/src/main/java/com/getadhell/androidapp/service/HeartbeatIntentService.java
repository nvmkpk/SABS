package com.getadhell.androidapp.service;

import android.app.IntentService;
import android.content.Intent;
import android.provider.Settings.Secure;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.getadhell.androidapp.App;
import com.getadhell.androidapp.BuildConfig;
import com.getadhell.androidapp.model.AndroidDeviceForm;
import com.getadhell.androidapp.net.CustomResponse;
import com.getadhell.androidapp.utils.DeviceUtils;

import java.io.IOException;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HeartbeatIntentService extends IntentService {

    private static final String TAG = HeartbeatIntentService.class.getCanonicalName();

    public HeartbeatIntentService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String androidId = Secure.getString(App.get().getApplicationContext().getContentResolver(), Secure.ANDROID_ID);
        AndroidDeviceForm androidDeviceForm = new AndroidDeviceForm();
        androidDeviceForm.adhellIntVersion = BuildConfig.VERSION_CODE;
        androidDeviceForm.androidId = androidId;
        androidDeviceForm.deviceManufacturer = android.os.Build.MANUFACTURER;
        androidDeviceForm.deviceModel = android.os.Build.MODEL;
        androidDeviceForm.sdkVersion = android.os.Build.VERSION.SDK_INT;
        androidDeviceForm.releaseVersion = android.os.Build.VERSION.RELEASE;
        androidDeviceForm.knoxStandardSdkVersion = DeviceUtils.getEnterpriseDeviceManager().getEnterpriseSdkVer().toString();


        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        ObjectMapper objectMapper = new ObjectMapper();
        OkHttpClient okHttpClient = new OkHttpClient();
        try {
            String json = objectMapper.writeValueAsString(androidDeviceForm);
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(BuildConfig.ADHELL_HEARTBEAT_URL)
                    .post(body)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            String stringResponse = response.body().string();
            CustomResponse<List<Integer>> customResponse =
                    objectMapper.readValue(stringResponse, new TypeReference<CustomResponse<List<Integer>>>() {
                    });
            // TODO: Next interation: download and update

        } catch (IOException e) {
            Log.e(TAG, "Failed heartbeat request", e);
        }

    }
}
