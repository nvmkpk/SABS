package com.getadhell.androidapp.blocker;

import android.app.enterprise.EnterpriseDeviceManager;
import android.app.enterprise.FirewallPolicy;
import android.content.Context;
import android.util.Log;

import com.getadhell.androidapp.contentprovider.AssetsContentBlockProvider;

import java.util.ArrayList;
import java.util.List;

public class ContentBlocker30 implements ContentBlocker {
    private final String LOG_TAG = ContentBlocker30.class.getCanonicalName();
    private AssetsContentBlockProvider assetsContentBlockProvider;
    private FirewallPolicy firewallPolicy;

    public ContentBlocker30(Context context) {
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
            disableBlocker();

            Log.d(LOG_TAG, "Loading block list rules");
            List<String> denyList = loadDenyList();
            if (denyList == null) {
                Log.w(LOG_TAG, "denyList is null");
                return false;
            }
            boolean isIpTablesEnabled = firewallPolicy.setURLFilterList(denyList);
            Log.i(LOG_TAG, "Is setUrlFilterList enabled: " + isIpTablesEnabled);
            boolean result2 = firewallPolicy.setURLFilterEnabled(true);
            Log.i(LOG_TAG, "setURLFilterEnabled >> " + result2);

            Log.d(LOG_TAG, "Leaving enableBlocker() method");
            return isIpTablesEnabled && result2;
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

            boolean isFilterListEmpty = firewallPolicy.setURLFilterList(new ArrayList<String>());
            boolean isUrlFilterDisabled = firewallPolicy.setURLFilterEnabled(false);
            Log.i(LOG_TAG, "Adhell disabled: " + (isFilterListEmpty && isUrlFilterDisabled));
            Log.d(LOG_TAG, "Leaving disableBlocker() method");
            return isFilterListEmpty && isUrlFilterDisabled;
        } catch (Throwable e) {
            Log.e(LOG_TAG, "Failed to disable ContentBlocker", e);
            Log.d(LOG_TAG, "Leaving disableBlocker() method");
            return false;
        }
    }

    @Override
    public boolean isEnabled() {
        return firewallPolicy.getURLFilterEnabled();
    }

    private List<String> loadDenyList() {
        List<String> urls = assetsContentBlockProvider.getBlockDb("block.json").urlsToBlock;
        for (int i = 0; i < urls.size(); i++) {
            urls.set(i, urls.get(i));
        }
        return urls;
    }
}
