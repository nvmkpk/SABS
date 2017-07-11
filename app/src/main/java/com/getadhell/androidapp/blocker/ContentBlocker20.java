package com.getadhell.androidapp.blocker;

import android.app.enterprise.EnterpriseDeviceManager;
import android.app.enterprise.FirewallPolicy;
import android.content.Context;
import android.util.Log;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.MainActivity;
import com.getadhell.androidapp.db.AppDatabase;
import com.getadhell.androidapp.db.entity.BlockUrl;
import com.getadhell.androidapp.db.entity.BlockUrlProvider;
import com.getadhell.androidapp.db.entity.UserBlockUrl;
import com.getadhell.androidapp.db.entity.WhiteUrl;
import com.getadhell.androidapp.utils.DeviceUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ContentBlocker20 implements ContentBlocker {
    private static ContentBlocker20 mInstance = null;
    private final String LOG_TAG = ContentBlocker20.class.getCanonicalName();
    private int urlBlockLimit = 10;
    private FirewallPolicy firewallPolicy;
    private AppDatabase appDatabase;

    private ContentBlocker20() {
        Context context = App.get().getApplicationContext();
        Log.d(LOG_TAG, "Entering constructor...");
        EnterpriseDeviceManager mEnterpriseDeviceManager = DeviceUtils.getEnterpriseDeviceManager();
        firewallPolicy = mEnterpriseDeviceManager.getFirewallPolicy();
        appDatabase = AppDatabase.getAppDatabase(context);
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
            List<String> denyList = new ArrayList<>();
            denyList.addAll(prepare());
            Log.i(LOG_TAG, "denyList size is: " + denyList.size());
            boolean isAdded = firewallPolicy.addIptablesRerouteRules(denyList);
            boolean isRulesEnabled = firewallPolicy.setIptablesOption(true);
            Log.d(LOG_TAG, "Re-route rules added: " + isAdded);

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


    private List<String> getDenyUrl() {
        Log.d(LOG_TAG, "Entering prepareUrls");
        BlockUrlProvider standardBlockUrlProvider =
                appDatabase.blockUrlProviderDao().getByUrl(MainActivity.ADHELL_STANDARD_PACKAGE);
        List<BlockUrl> standardList = appDatabase.blockUrlDao().getUrlsByProviderId(standardBlockUrlProvider.id);
        List<UserBlockUrl> userBlockUrls = appDatabase.userBlockUrlDao().getAll2();

        List<String> denyList = new ArrayList<>();
        for (UserBlockUrl userBlockUrl : userBlockUrls) {
            denyList.add(userBlockUrl.url);
        }
        for (BlockUrl blockUrl : standardList) {
            denyList.add(blockUrl.url);
        }

        List<WhiteUrl> whiteUrls = appDatabase.whiteUrlDao().getAll2();

        List<String> whiteUrlsString = new ArrayList<>();
        for (WhiteUrl whiteUrl : whiteUrls) {
            whiteUrlsString.add(whiteUrl.url);
        }
        denyList.removeAll(whiteUrlsString);
        denyList = denyList.subList(0, urlBlockLimit);
        Log.i(LOG_TAG, denyList.toString());
        return denyList;
    }

    private Set<String> prepare() {
        List<String> urlsToCheck = getDenyUrl();
        Set<String> ipsToBlock = new HashSet<>();
        for (String url : urlsToCheck) {
            if (ipsToBlock.size() > 625) {
                break;
            }
            Log.i(LOG_TAG, "Checking url: " + url);
            try {
                InetAddress[] addresses = InetAddress.getAllByName(url);
                for (InetAddress inetAddress : addresses) {
                    ipsToBlock.add(inetAddress.getHostAddress() + ":*;127.0.0.1:80");
                    Log.i(LOG_TAG, "Address: " + inetAddress.getHostAddress());
                }
            } catch (UnknownHostException e) {
                Log.e(LOG_TAG, "Failed to resolve: " + url, e);
            }
        }
        return ipsToBlock;
    }
}
