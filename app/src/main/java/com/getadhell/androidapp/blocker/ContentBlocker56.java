package com.getadhell.androidapp.blocker;

import android.app.enterprise.EnterpriseDeviceManager;
import android.content.Context;
import android.util.Log;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.contentprovider.ServerContentBlockProvider;
import com.getadhell.androidapp.utils.DeviceUtils;
import com.sec.enterprise.AppIdentity;
import com.sec.enterprise.firewall.DomainFilterRule;
import com.sec.enterprise.firewall.Firewall;
import com.sec.enterprise.firewall.FirewallResponse;

import java.util.ArrayList;
import java.util.List;

public class ContentBlocker56 implements ContentBlocker {
    private static ContentBlocker56 mInstance = null;
    private final String LOG_TAG = ContentBlocker56.class.getCanonicalName();
    private ServerContentBlockProvider contentBlockProvider;
    private Firewall mFirewall;


    private ContentBlocker56() {
        Context context = App.get().getApplicationContext();
        EnterpriseDeviceManager mEnterpriseDeviceManager = DeviceUtils.getEnterpriseDeviceManager();
        contentBlockProvider = new ServerContentBlockProvider(context.getFilesDir());
        mFirewall = mEnterpriseDeviceManager.getFirewall();
    }

    public static ContentBlocker56 getInstance() {
        if (mInstance == null) {
            mInstance = getSync();
        }
        return mInstance;
    }

    private static synchronized ContentBlocker56 getSync() {
        if (mInstance == null) {
            mInstance = new ContentBlocker56();
        }
        return mInstance;
    }

    @Override
    public boolean enableBlocker() {
        if (isEnabled()) {
            disableBlocker();
        }
        Log.d(LOG_TAG, "Loading block.js");
        List<String> denyList = loadDenyList();
        if (denyList == null) {
            Log.w(LOG_TAG, "denyList is null");
            return false;
        }
        List<String> allowList = new ArrayList<String>();
        List<DomainFilterRule> rules = new ArrayList<DomainFilterRule>();
        AppIdentity appIdentity = new AppIdentity("*", null);
        rules.add(new DomainFilterRule(appIdentity, denyList, allowList));
        List<String> superAllow = new ArrayList<String>();
        superAllow.add("*");
        for (String app : contentBlockProvider.loadAllowApps()) {
            rules.add(new DomainFilterRule(new AppIdentity(app, null), new ArrayList<String>(), superAllow));
        }
        try {
            FirewallResponse[] response = mFirewall.addDomainFilterRules(rules);
            if (!mFirewall.isFirewallEnabled()) {
                mFirewall.enableFirewall(true);
            }
            if (!mFirewall.isDomainFilterReportEnabled()) {
                Log.d(LOG_TAG, "Enabling filewall report");
                mFirewall.enableDomainFilterReport(true);
            }
            if (FirewallResponse.Result.SUCCESS == response[0].getResult()) {
                Log.i(LOG_TAG, "Adhell enabled " + response[0].getMessage());
                return true;
            } else {
                Log.i(LOG_TAG, "Adhell enabling failed " + response[0].getMessage());
                return false;
            }
        } catch (SecurityException ex) {
            Log.e(LOG_TAG, "Adhell enabling failed", ex);
            return false;
        }
    }

    @Override
    public boolean disableBlocker() {
        FirewallResponse[] response;
        try {
            response = mFirewall.removeDomainFilterRules(DomainFilterRule.CLEAR_ALL);
            Log.i(LOG_TAG, "disableBlocker " + response[0].getMessage());
            if (mFirewall.isFirewallEnabled()) {
                mFirewall.enableFirewall(false);
            }
            if (mFirewall.isDomainFilterReportEnabled()) {
                mFirewall.enableDomainFilterReport(false);
            }
        } catch (SecurityException ex) {
            Log.e(LOG_TAG, "Failed to removeDomainFilterRules", ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean isEnabled() {
        return mFirewall.isFirewallEnabled();
    }

    private List<String> loadDenyList() {
        List<String> urls = contentBlockProvider.loadBlockDb().urlsToBlock;
        urls.addAll(DeviceUtils.loadCustomBlockedUrls());
        for (int i = 0; i < urls.size(); i++) {
            urls.set(i, "*" + urls.get(i) + "*");
        }
        return urls;
    }
}
