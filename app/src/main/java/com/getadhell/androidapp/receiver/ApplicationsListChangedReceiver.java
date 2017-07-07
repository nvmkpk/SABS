package com.getadhell.androidapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.widget.Toast;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.db.AppDatabase;
import com.getadhell.androidapp.db.entity.AppInfo;
import com.getadhell.androidapp.utils.AppsListDBInitializer;

import java.util.List;

public class ApplicationsListChangedReceiver extends BroadcastReceiver
{

    @Override
    public void onReceive(Context context, Intent intent)
    {
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
