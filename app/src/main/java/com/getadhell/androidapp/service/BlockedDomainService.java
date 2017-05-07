package com.getadhell.androidapp.service;

import android.app.IntentService;
import android.app.enterprise.EnterpriseDeviceManager;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.utils.AdhellDatabaseHelper;
import com.getadhell.androidapp.utils.DeviceUtils;
import com.sec.enterprise.firewall.Firewall;

public class BlockedDomainService extends IntentService {
    public static final String TAG = BlockedDomainService.class.getCanonicalName();

    public BlockedDomainService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate(); // if you override onCreate(), make sure to call super().
        // If a Context object is needed, call getApplicationContext() here.
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "Saving domain list");
        EnterpriseDeviceManager mEnterpriseDeviceManager = DeviceUtils.getEnterpriseDeviceManager();
        Firewall firewall = mEnterpriseDeviceManager.getFirewall();
        AdhellDatabaseHelper.getInstance(App.get().getApplicationContext())
                .addBlockedDomains(firewall.getDomainFilterReport(null));
    }
}
