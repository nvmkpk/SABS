package com.getadhell.androidapp.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.R;
import com.getadhell.androidapp.deviceadmin.DeviceAdminInteractor;

public class AdhellDownloadBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = AdhellDownloadBroadcastReceiver.class.getCanonicalName();
    private Context mContext;

    public AdhellDownloadBroadcastReceiver() {
        super();
        mContext = App.get().getApplicationContext();

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive download completed");
        long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
        SharedPreferences sharedPref =
                App.get().getApplicationContext()
                        .getSharedPreferences(context.getString(R.string.download_manager_sharedPrefs),
                                Context.MODE_PRIVATE);
        long savedReferenceId = sharedPref.getLong(context.getString(R.string.download_manager_reference_id), -2);
        if (referenceId == savedReferenceId) {
            DeviceAdminInteractor deviceAdminInteractor = DeviceAdminInteractor.getInstance();
            if (deviceAdminInteractor.isKnoxEnbaled()) {
                Log.i(TAG, "Knox enabled");

                String downloadDir = context.getExternalFilesDir(null).toString();
                Log.i(TAG, "get dit: " + downloadDir);

                final PackageManager pm = mContext.getPackageManager();
                String apkName = "example.apk";
                String fullPath = downloadDir;
                PackageInfo info = pm.getPackageArchiveInfo(fullPath, 0);
                Toast.makeText(mContext, "VersionCode : " + info.versionCode + ", VersionName : " + info.versionName, Toast.LENGTH_LONG).show();

                boolean isInstalled = deviceAdminInteractor.installApk(downloadDir + "/adhell.apk");
                Log.i(TAG, "Path to: " + downloadDir + "/adhell.apk");
                if (!isInstalled) {
                    Toast.makeText(context, "Failed to update Adhell.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(context, "Adhell app updated!", Toast.LENGTH_LONG).show();
                }
            } else {
                Log.i(TAG, "Knox is disabled");
            }
        }
    }
}
