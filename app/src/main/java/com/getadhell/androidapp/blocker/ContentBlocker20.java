package com.getadhell.androidapp.blocker;

import android.app.enterprise.EnterpriseDeviceManager;
import android.app.enterprise.FirewallPolicy;
import android.content.Context;
import android.util.Log;

import com.getadhell.androidapp.contentprovider.AssetsContentBlockProvider;

import java.util.List;

public class ContentBlocker20 implements ContentBlocker {
    private static final int URL_BLOCK_LIMIT = 1500;
    private final String LOG_TAG = ContentBlocker20.class.getCanonicalName();
    private AssetsContentBlockProvider assetsContentBlockProvider;
    private FirewallPolicy firewallPolicy;

    public ContentBlocker20(Context context) {
        Log.d(LOG_TAG, "Entering constructor...");
        EnterpriseDeviceManager mEnterpriseDeviceManager = (EnterpriseDeviceManager)
                context.getSystemService(EnterpriseDeviceManager.ENTERPRISE_POLICY_SERVICE);
        assetsContentBlockProvider = new AssetsContentBlockProvider(context);
        firewallPolicy = mEnterpriseDeviceManager.getFirewallPolicy();
        Log.d(LOG_TAG, "Leaving constructor.");
    }

    @Override
    public boolean enableBlocker() {
        Log.d(LOG_TAG, "Entering enableBlocker() method...");
        try {
            Log.d(LOG_TAG, "Check if Adhell enabled. Disable if true");
            if (isEnabled()) {
                disableBlocker();
            }
            Log.d(LOG_TAG, "Loading block list rules");
            List<String> denyList = loadDenyList();
            boolean isAdded = firewallPolicy.setIptablesRerouteRules(denyList);
            if (denyList == null) {
                Log.w(LOG_TAG, "denyList is null");
            }
            Log.d(LOG_TAG, "Re-route rules added: " + isAdded);
            boolean isRulesEnabled = firewallPolicy.setIptablesOption(true);
            Log.d(LOG_TAG, "Rules enabled: " + isRulesEnabled);
            Log.d(LOG_TAG, "Leaving enableBlocker() method");
            return isAdded;
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
        List<String> urls = assetsContentBlockProvider.getBlockDb("block.json").urlsToBlock;
        for (int i = 0; i < urls.size(); i++) {
            urls.set(i, urls.get(i) + ":*;127.0.0.1:80");
            if (i == URL_BLOCK_LIMIT) {
                break;
            }
        }
        return urls.subList(0, URL_BLOCK_LIMIT);
    }
}
