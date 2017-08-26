package com.fiendfyre.AdHell2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;

import com.fiendfyre.AdHell2.App;
import com.fiendfyre.AdHell2.db.AppDatabase;
import com.fiendfyre.AdHell2.db.entity.AppInfo;
import com.fiendfyre.AdHell2.utils.AppsListDBInitializer;

import java.util.List;

public class ApplicationsListChangedReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AsyncTask.execute(() ->
        {
            String packageName = intent.getData().getEncodedSchemeSpecificPart();
            AppDatabase mDb = AppDatabase.getAppDatabase(App.get().getApplicationContext());
            List<AppInfo> packageList = mDb.applicationInfoDao().getAll();
            if (packageList.size() == 0) return;
            if (intent.getAction().equalsIgnoreCase("android.intent.action.PACKAGE_ADDED"))
                mDb.applicationInfoDao().insert(AppsListDBInitializer.getInstance()
                        .generateAppInfo(context.getPackageManager(), packageName));
            else mDb.applicationInfoDao().deleteAppInfoByPackageName(packageName);
        });
    }
}
