package com.getadhell.androidapp.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.R;
import com.getadhell.androidapp.deviceadmin.DeviceAdminInteractor;

public class AdhellDownloadBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = AdhellDownloadBroadcastReceiver.class.getCanonicalName();

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
            DownloadManager.Query referenceIdQuery = new DownloadManager.Query();
            //set the query filter to our previously Enqueued download
            referenceIdQuery.setFilterById(referenceId);

            //Query the download manager about downloads that have been requested.
//            DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
//            Cursor cursor = downloadManager.query(referenceIdQuery);
//            String file = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME)

            DeviceAdminInteractor deviceAdminInteractor = DeviceAdminInteractor.getInstance();
            if (deviceAdminInteractor.isKnoxEnbaled()) {
                Log.i(TAG, "Knox enabled");

                String downloadDir = context.getExternalFilesDir(null).toString();
                Log.i(TAG, "get dit: " + downloadDir);
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
