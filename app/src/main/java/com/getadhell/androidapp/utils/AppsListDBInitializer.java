package com.getadhell.androidapp.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.db.AppDatabase;
import com.getadhell.androidapp.db.entity.AppInfo;

import java.util.ArrayList;
import java.util.List;


public class AppsListDBInitializer
{
    private static final AppsListDBInitializer instance = new AppsListDBInitializer();

    public static AppsListDBInitializer getInstance()
    {
        return instance;
    }

    private AppsListDBInitializer() {}

    public void fillPackageDb(PackageManager packageManager)
    {
        List<ApplicationInfo> applicationsInfo = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        List<AppInfo> appsInfo = new ArrayList<>();
        int id = 0;
        for (ApplicationInfo applicationInfo : applicationsInfo)
        {
            String pckg = App.get().getApplicationContext().getPackageName();
            if (applicationInfo.packageName.equals(pckg)) continue;
            AppInfo appInfo = new AppInfo();
            appInfo.id = id++;
            appInfo.appName = packageManager.getApplicationLabel(applicationInfo).toString();
            appInfo.packageName = applicationInfo.packageName;
            int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
            appInfo.system = (applicationInfo.flags & mask) != 0;
            try { appInfo.installTime = packageManager.getPackageInfo(applicationInfo.packageName, 0).firstInstallTime; }
            catch (PackageManager.NameNotFoundException e) { e.printStackTrace(); appInfo.installTime = 0; }
            appsInfo.add(appInfo);
        }
        AppDatabase.getAppDatabase(App.get().getApplicationContext()).applicationInfoDao().insertAll(appsInfo);
    }

    public AppInfo generateAppInfo(PackageManager packageManager, String packageName)
    {
        AppInfo appInfo = new AppInfo();
        try
        {
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            AppDatabase mDb = AppDatabase.getAppDatabase(App.get().getApplicationContext());
            appInfo.id = mDb.applicationInfoDao().getMaxId() + 1;
            appInfo.packageName = packageName;
            appInfo.appName = packageManager.getApplicationLabel(applicationInfo).toString();
            int mask = ApplicationInfo.FLAG_SYSTEM | ApplicationInfo.FLAG_UPDATED_SYSTEM_APP;
            appInfo.system = (applicationInfo.flags & mask) != 0;
            appInfo.installTime = packageManager.getPackageInfo(packageName, 0).firstInstallTime;
        }
        catch (Exception e) { e.printStackTrace(); }
        return appInfo;
    }
}
