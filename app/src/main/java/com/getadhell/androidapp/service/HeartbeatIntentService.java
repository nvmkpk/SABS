package com.getadhell.androidapp.service;

import android.app.DownloadManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.getadhell.androidapp.App;
import com.getadhell.androidapp.BuildConfig;
import com.getadhell.androidapp.R;
import com.getadhell.androidapp.model.AndroidDeviceForm;
import com.getadhell.androidapp.net.AdhellInfoResponse;
import com.getadhell.androidapp.net.CustomResponse;
import com.getadhell.androidapp.utils.DeviceUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class HeartbeatIntentService extends IntentService {

    private static final String TAG = HeartbeatIntentService.class.getCanonicalName();
    private static final String PREF_UNIQUE_ID = "PREF_UNIQUE_ID";
    private static String uniqueID = null;
    private OkHttpClient mOkHttpClient;
    private ObjectMapper mObjectMapper;
    private Context mContext;

    public HeartbeatIntentService() {
        super(TAG);
        mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(20, TimeUnit.SECONDS)
                .readTimeout(20, TimeUnit.SECONDS)
                .build();
        mObjectMapper = new ObjectMapper();
        mContext = App.get().getApplicationContext();

    }

    public synchronized static String id(Context context) {
        if (uniqueID == null) {
            SharedPreferences sharedPrefs = context.getSharedPreferences(
                    PREF_UNIQUE_ID, Context.MODE_PRIVATE);
            uniqueID = sharedPrefs.getString(PREF_UNIQUE_ID, null);
            if (uniqueID == null) {
                uniqueID = UUID.randomUUID().toString();
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString(PREF_UNIQUE_ID, uniqueID);
                editor.apply();
            }
        }
        return uniqueID;
    }

    private void makeUpdateRequest() {
        File applicationFolder = mContext.getExternalFilesDir(null);
        if (applicationFolder == null || !applicationFolder.canWrite()) {
            return;
        }
        Log.d(TAG, "Starting makeUpdateRequest()");
        Request request = new Request.Builder()
                .url("http://getadhell.com/adhell-info.json")
                .build();
        try {
            Response response = mOkHttpClient.newCall(request).execute();
            String stringResponse = response.body().string();
            AdhellInfoResponse adhellInfoResponse = mObjectMapper.readValue(stringResponse, AdhellInfoResponse.class);
            Log.i(TAG, "Adhell version: " + adhellInfoResponse.appVersion);
            if (adhellInfoResponse.appVersion > BuildConfig.VERSION_CODE) {
                String downloadDir = applicationFolder.toString();
                String apkFilePath = downloadDir + "/adhell.apk";
                File file = new File(apkFilePath);
                if (file.exists()) {
                    boolean isDeleted = file.delete();
                    Log.i(TAG, "Apk deleted: " + isDeleted);
                }
                Log.i(TAG, "File path: " + downloadDir);
                mContext.deleteFile("adhell.apk");
                long referenceId = startDownload(adhellInfoResponse.appDownloadLink);
                Log.i(TAG, "Reference ID: " + referenceId);
                SharedPreferences sharedPref =
                        mContext.getSharedPreferences(getString(R.string.download_manager_sharedPrefs),
                                Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putLong(getString(R.string.download_manager_reference_id), referenceId);
                editor.apply();
            }
        } catch (IOException e) {
            Log.e(TAG, "Failed to make update request", e);
        }
    }

    private long startDownload(String url) {
        Uri uri = Uri.parse(url);
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(uri);
        request.setTitle("New Adhell version");
        request.setDescription("After download new version will be installed.");
        request.setDestinationInExternalFilesDir(mContext, null, "adhell.apk");
        long downloadReference = downloadManager.enqueue(request);
        Log.i(TAG, "ReferenceId: " + downloadReference);
        return downloadReference;
    }


    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "Starting hearbeat service");
//        makeUpdateRequest();
//        makeHearbeatRequest();
    }

    private void makeHearbeatRequest() {
        AndroidDeviceForm androidDeviceForm = new AndroidDeviceForm();
        androidDeviceForm.adhellIntVersion = BuildConfig.VERSION_CODE;
        androidDeviceForm.deviceManufacturer = android.os.Build.MANUFACTURER;
        androidDeviceForm.deviceModel = android.os.Build.MODEL;
        androidDeviceForm.sdkVersion = android.os.Build.VERSION.SDK_INT;
        androidDeviceForm.releaseVersion = android.os.Build.VERSION.RELEASE;
        androidDeviceForm.knoxStandardSdkVersion = DeviceUtils.getEnterpriseDeviceManager().getEnterpriseSdkVer().toString();
        androidDeviceForm.appId = id(mContext);

        final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        try {
            String json = mObjectMapper.writeValueAsString(androidDeviceForm);
            RequestBody body = RequestBody.create(JSON, json);
            Request request = new Request.Builder()
                    .url(BuildConfig.ADHELL_HEARTBEAT_URL)
                    .post(body)
                    .build();
            Response response = mOkHttpClient.newCall(request).execute();
            String stringResponse = response.body().string();
            CustomResponse<List<Integer>> customResponse =
                    mObjectMapper.readValue(stringResponse, new TypeReference<CustomResponse<List<Integer>>>() {
                    });
            Log.i(TAG, "Reponse received");
        } catch (IOException e) {
            Log.e(TAG, "Failed heartbeat request", e);
        }
    }
}
