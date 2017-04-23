package com.getadhell.androidapp.blocker;

import android.app.enterprise.EnterpriseDeviceManager;
import android.app.enterprise.FirewallPolicy;
import android.content.Context;
import android.util.Log;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.contentprovider.ServerContentBlockProvider;
import com.getadhell.androidapp.utils.DeviceUtils;

import java.util.List;

public class ContentBlocker20 implements ContentBlocker {
    private static ContentBlocker20 mInstance = null;
    private final String LOG_TAG = ContentBlocker20.class.getCanonicalName();
    private int urlBlockLimit = 1625;
    private ServerContentBlockProvider contentBlockProvider;
    private FirewallPolicy firewallPolicy;

    private ContentBlocker20() {
        Context context = App.get().getApplicationContext();
        Log.d(LOG_TAG, "Entering constructor...");
        EnterpriseDeviceManager mEnterpriseDeviceManager = DeviceUtils.getEnterpriseDeviceManager();
        contentBlockProvider = new ServerContentBlockProvider(context.getFilesDir());
        firewallPolicy = mEnterpriseDeviceManager.getFirewallPolicy();
        Log.d(LOG_TAG, "Number of urls to block: " + urlBlockLimit);
        Log.d(LOG_TAG, "Leaving constructor.");
    }

    public static ContentBlocker20 getInstance() {
        if (mInstance == null) {
            mInstance = getSync();
        }
        return mInstance;
    }

    private static synchronized ContentBlocker20 getSync() {
        if (mInstance == null) {
            mInstance = new ContentBlocker20();
        }
        return mInstance;
    }

    public void setUrlBlockLimit(int urlBlockLimit) {
        this.urlBlockLimit = urlBlockLimit;
    }

    @Override
    public boolean enableBlocker() {
        disableBlocker();
        Log.d(LOG_TAG, "Entering enableBlocker() method...");
        try {
            Log.d(LOG_TAG, "Check if Adhell enabled. Disable if true");
            Log.d(LOG_TAG, "Loading block list rules");
            List<String> denyList = loadDenyList();
            boolean isAdded = firewallPolicy.addIptablesRerouteRules(denyList);
            Log.d(LOG_TAG, "Re-route rules added: " + isAdded);
            boolean isRulesEnabled = firewallPolicy.setIptablesOption(true);
            Log.d(LOG_TAG, "Rules enabled: " + isRulesEnabled);
            Log.d(LOG_TAG, "Leaving enableBlocker() method");
            return isRulesEnabled;
        } catch (Throwable e) {
            Log.e(LOG_TAG, "Failed to enable Adhell:", e);
            Log.d(LOG_TAG, "Leaving enableBlocker() method");
            return false;
        }
    }

    @Override
    public boolean disableBlocker() {
        Log.d(LOG_TAG, "Entering disableBlocker() method...");
        try {

            firewallPolicy.cleanIptablesAllowRules();
            firewallPolicy.cleanIptablesDenyRules();
            firewallPolicy.cleanIptablesProxyRules();
            firewallPolicy.cleanIptablesRedirectExceptionsRules();
            firewallPolicy.cleanIptablesRerouteRules();
            firewallPolicy.removeIptablesRules();
            boolean isDisabled = firewallPolicy.setIptablesOption(false);
            return isDisabled;
        } catch (Throwable e) {
            Log.e(LOG_TAG, "Failed to disable ContentBlocker", e);
            Log.d(LOG_TAG, "Leaving disableBlocker() method");
            return false;
        }
    }

    @Override
    public boolean isEnabled() {
        return firewallPolicy.getIptablesOption();
    }

    private List<String> loadDenyList() {
        List<String> urls = contentBlockProvider.loadBlockDb().urlsToBlock;
        for (int i = 0; i < urls.size(); i++) {
            urls.set(i, urls.get(i) + ":*;127.0.0.1:80");
            if (i == urlBlockLimit) {
                break;
            }
        }
        return urls.subList(0, urlBlockLimit);
    }
}
