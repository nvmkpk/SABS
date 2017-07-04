package com.getadhell.androidapp.service;

import android.app.IntentService;
import android.app.enterprise.EnterpriseDeviceManager;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.blocker.ContentBlocker;
import com.getadhell.androidapp.db.AppDatabase;
import com.getadhell.androidapp.db.entity.ReportBlockedUrl;
import com.getadhell.androidapp.deviceadmin.DeviceAdminInteractor;
import com.getadhell.androidapp.utils.DeviceUtils;
import com.sec.enterprise.firewall.DomainFilterReport;
import com.sec.enterprise.firewall.Firewall;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BlockedDomainService extends IntentService {
    public static final String TAG = BlockedDomainService.class.getCanonicalName();

    public BlockedDomainService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (!DeviceUtils.isContentBlockerSupported()) {
            return;
        }
        if (!DeviceAdminInteractor.getInstance().isKnoxEnbaled()) {
            return;
        }
        ContentBlocker contentBlocker = DeviceUtils.getContentBlocker();
        if (contentBlocker == null || !contentBlocker.isEnabled()) {
            return;
        }

        Log.d(TAG, "Saving domain list");
        EnterpriseDeviceManager mEnterpriseDeviceManager = DeviceUtils.getEnterpriseDeviceManager();
        Firewall firewall = mEnterpriseDeviceManager.getFirewall();

        AppDatabase appDatabase = AppDatabase.getAppDatabase(App.get().getApplicationContext());
        Date yesterday = new Date(System.currentTimeMillis() - 24 * 3600 * 1000);
        appDatabase.reportBlockedUrlDao().deleteBefore(yesterday);

        ReportBlockedUrl lastBlockedUrl = appDatabase.reportBlockedUrlDao().getLastBlockedDomain();
        long lastBlockedTimestamp = 0;
        if (lastBlockedUrl != null) {
            lastBlockedTimestamp = lastBlockedUrl.blockDate.getTime() / 1000;
        }

        List<ReportBlockedUrl> reportBlockedUrls = new ArrayList<>();

        for (DomainFilterReport b : firewall.getDomainFilterReport(null)) {
            if (b.getTimeStamp() > lastBlockedTimestamp) {
                ReportBlockedUrl reportBlockedUrl =
                        new ReportBlockedUrl(b.getDomainUrl(), b.getPackageName(), new Date(b.getTimeStamp() * 1000));
                reportBlockedUrls.add(reportBlockedUrl);
            }
        }
        appDatabase.reportBlockedUrlDao().insertAll(reportBlockedUrls);
    }
}
