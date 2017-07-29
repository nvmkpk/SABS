package com.getadhell.androidapp.blocker;

import android.support.annotation.Nullable;
import android.util.Log;
import android.util.Patterns;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.MainActivity;
import com.getadhell.androidapp.db.AppDatabase;
import com.getadhell.androidapp.db.entity.AppInfo;
import com.getadhell.androidapp.db.entity.BlockUrl;
import com.getadhell.androidapp.db.entity.BlockUrlProvider;
import com.getadhell.androidapp.db.entity.UserBlockUrl;
import com.getadhell.androidapp.db.entity.WhiteUrl;
import com.sec.enterprise.AppIdentity;
import com.sec.enterprise.firewall.DomainFilterRule;
import com.sec.enterprise.firewall.Firewall;
import com.sec.enterprise.firewall.FirewallResponse;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

public class ContentBlocker56 implements ContentBlocker {
    private static ContentBlocker56 mInstance = null;
    private final String TAG = ContentBlocker56.class.getCanonicalName();

    @Nullable
    @Inject
    Firewall mFirewall;
    @Inject
    AppDatabase appDatabase;
    private int urlBlockLimit = 2700;

    private ContentBlocker56() {
        App.get().getAppComponent().inject(this);
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
        BlockUrlProvider standardBlockUrlProvider =
                appDatabase.blockUrlProviderDao().getByUrl(MainActivity.ADHELL_STANDARD_PACKAGE);
        List<BlockUrl> standardList = appDatabase.blockUrlDao().getUrlsByProviderId(standardBlockUrlProvider.id);

        Set<BlockUrl> finalBlockList = new HashSet<>();
        finalBlockList.addAll(standardList);
        List<BlockUrlProvider> blockUrlProviders = appDatabase.blockUrlProviderDao().getBlockUrlProviderBySelectedFlag(1);

        for (BlockUrlProvider blockUrlProvider : blockUrlProviders) {
            if (blockUrlProvider.url.equals(MainActivity.ADHELL_STANDARD_PACKAGE)) {
                continue;
            }
            Log.i(TAG, "Included url provider: " + blockUrlProvider.url);
            List<BlockUrl> blockUrls = appDatabase.blockUrlDao().getUrlsByProviderId(blockUrlProvider.id);
            if (finalBlockList.size() + blockUrls.size() <= this.urlBlockLimit - 100) {
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
        List<WhiteUrl> whiteUrls = appDatabase.whiteUrlDao().getAll2();

        List<String> whiteUrlsString = new ArrayList<>();
        for (WhiteUrl whiteUrl : whiteUrls) {
            whiteUrlsString.add(whiteUrl.url);
        }

        List<String> denyList = new ArrayList<>();
        for (BlockUrl blockUrl : finalBlockList) {
            if (Patterns.WEB_URL.matcher(blockUrl.url).matches()) {
                if (whiteUrlsString.contains(blockUrl.url)) {
                    continue;
                }
                denyList.add("*" + blockUrl.url + "*");
            }
        }

        List<UserBlockUrl> userBlockUrls = appDatabase.userBlockUrlDao().getAll2();

        if (userBlockUrls != null && userBlockUrls.size() > 0) {
            Log.i(TAG, "UserBlockUrls size: " + userBlockUrls.size());
            for (UserBlockUrl userBlockUrl : userBlockUrls) {
                if (Patterns.WEB_URL.matcher(userBlockUrl.url).matches()) {
                    denyList.add("*" + userBlockUrl.url + "*");
                    Log.i(TAG, "UserBlockUrl: " + userBlockUrl.url);
                }
            }
        } else {
            Log.i(TAG, "UserBlockUrls is empty.");
        }

        Log.d(TAG, "Number of block list: " + denyList.size());
        List<String> allowList = new ArrayList<>();
        List<DomainFilterRule> rules = new ArrayList<>();
        AppIdentity appIdentity = new AppIdentity("*", null);
        rules.add(new DomainFilterRule(appIdentity, denyList, allowList));
        List<String> superAllow = new ArrayList<>();
        superAllow.add("*");
        List<AppInfo> appInfos = appDatabase.applicationInfoDao().getWhitelistedApps();
        Log.d(TAG, "Whitelisted apps size: " + appInfos.size());
        for (AppInfo app : appInfos) {
            Log.d(TAG, app.packageName);
            rules.add(new DomainFilterRule(new AppIdentity(app.packageName, null), new ArrayList<>(), superAllow));
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
