package com.getadhell.androidapp.blocker;

import android.app.enterprise.EnterpriseDeviceManager;
import android.content.Context;
import android.util.Log;
import android.util.Patterns;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.MainActivity;
import com.getadhell.androidapp.contentprovider.ServerContentBlockProvider;
import com.getadhell.androidapp.db.AppDatabase;
import com.getadhell.androidapp.db.entity.BlockUrl;
import com.getadhell.androidapp.db.entity.BlockUrlProvider;
import com.getadhell.androidapp.utils.DeviceUtils;
import com.sec.enterprise.AppIdentity;
import com.sec.enterprise.firewall.DomainFilterRule;
import com.sec.enterprise.firewall.Firewall;
import com.sec.enterprise.firewall.FirewallResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContentBlocker56 implements ContentBlocker {
    private static ContentBlocker56 mInstance = null;
    private final String TAG = ContentBlocker56.class.getCanonicalName();
    private ServerContentBlockProvider contentBlockProvider;
    private Firewall mFirewall;
    private int urlBlockLimit = 2700;


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
        AppDatabase appDatabase = AppDatabase.getAppDatabase(App.get().getApplicationContext());

        BlockUrlProvider standardBlockUrlProvider =
                appDatabase.blockUrlProviderDao().getByUrl(MainActivity.ADHELL_STANDARD_PACKAGE);
        List<BlockUrl> standardList = appDatabase.blockUrlDao().getUrlsByProviderId(standardBlockUrlProvider.id);
        List<BlockUrl> customBlockUrlList = appDatabase.blockUrlDao().getUrlsByProviderId(-1);
        Set<BlockUrl> finalBlockList = new HashSet<>();
        finalBlockList.addAll(standardList);
        finalBlockList.addAll(customBlockUrlList);
        List<BlockUrlProvider> blockUrlProviders = appDatabase.blockUrlProviderDao().getBlockUrlProviderBySelectedFlag(1);

        for (BlockUrlProvider blockUrlProvider : blockUrlProviders) {
            if (blockUrlProvider.url.equals(MainActivity.ADHELL_STANDARD_PACKAGE)) {
                continue;
            }
            Log.i(TAG, "Included url provider: " + blockUrlProvider.url);
            List<BlockUrl> blockUrls = appDatabase.blockUrlDao().getUrlsByProviderId(blockUrlProvider.id);
            if (finalBlockList.size() + blockUrls.size() <= this.urlBlockLimit) {
                finalBlockList.addAll(blockUrls);
            } else {
                int remain = this.urlBlockLimit - finalBlockList.size();
                if (remain < blockUrls.size()) {
                    blockUrls = blockUrls.subList(0, remain);
                }
                finalBlockList.addAll(blockUrls);
                break;
            }
        }
        List<String> denyList = new ArrayList<>();
        for (BlockUrl blockUrl : finalBlockList) {
            if (Patterns.WEB_URL.matcher(blockUrl.url).matches()) {
                denyList.add("*" + blockUrl.url + "*");
            }
        }
        Log.d(TAG, "Number of block list: " + denyList.size());
        List<String> allowList = new ArrayList<>();
        List<DomainFilterRule> rules = new ArrayList<>();
        AppIdentity appIdentity = new AppIdentity("*", null);
        rules.add(new DomainFilterRule(appIdentity, denyList, allowList));
        List<String> superAllow = new ArrayList<>();
        superAllow.add("*");
        for (String app : contentBlockProvider.loadAllowApps()) {
            rules.add(new DomainFilterRule(new AppIdentity(app, null), new ArrayList<>(), superAllow));
        }
        try {
            FirewallResponse[] response = mFirewall.addDomainFilterRules(rules);
            if (!mFirewall.isFirewallEnabled()) {
                mFirewall.enableFirewall(true);
            }
            if (!mFirewall.isDomainFilterReportEnabled()) {
                Log.d(TAG, "Enabling filewall report");
                mFirewall.enableDomainFilterReport(true);
            }
            if (FirewallResponse.Result.SUCCESS == response[0].getResult()) {
                Log.i(TAG, "Adhell enabled " + response[0].getMessage());
                return true;
            } else {
                Log.i(TAG, "Adhell enabling failed " + response[0].getMessage());
                return false;
            }
        } catch (SecurityException ex) {
            Log.e(TAG, "Adhell enabling failed", ex);
            return false;
        }
    }

    @Override
    public boolean disableBlocker() {
        FirewallResponse[] response;
        try {
            response = mFirewall.removeDomainFilterRules(DomainFilterRule.CLEAR_ALL);
            Log.i(TAG, "disableBlocker " + response[0].getMessage());
            if (mFirewall.isFirewallEnabled()) {
                mFirewall.enableFirewall(false);
            }
            if (mFirewall.isDomainFilterReportEnabled()) {
                mFirewall.enableDomainFilterReport(false);
            }
        } catch (SecurityException ex) {
            Log.e(TAG, "Failed to removeDomainFilterRules", ex);
            return false;
        }
        return true;
    }

    @Override
    public boolean isEnabled() {
        return mFirewall.isFirewallEnabled();
    }

    public void setUrlBlockLimit(int urlBlockLimit) {
        this.urlBlockLimit = urlBlockLimit;
    }

}
