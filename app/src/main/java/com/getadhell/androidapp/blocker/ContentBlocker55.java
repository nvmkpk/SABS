package com.getadhell.androidapp.blocker;

import android.app.enterprise.EnterpriseDeviceManager;
import android.content.Context;
import android.util.Log;

import com.getadhell.androidapp.contentprovider.AssetsContentBlockProvider;
import com.getadhell.androidapp.contentprovider.ServerContentBlockProvider;
import com.sec.enterprise.firewall.Firewall;
import com.sec.enterprise.firewall.FirewallResponse;
import com.sec.enterprise.firewall.FirewallRule;

import java.util.List;

public class ContentBlocker55 implements ContentBlocker {
    private static final int URL_BLOCK_LIMIT = 2300;

    private final String LOG_TAG = ContentBlocker55.class.getCanonicalName();
    private ServerContentBlockProvider contentBlockProvider;
    private Firewall mFirewall;

    public ContentBlocker55(Context context) {
        Log.d(LOG_TAG, "Entering constructor...");
        EnterpriseDeviceManager mEnterpriseDeviceManager = (EnterpriseDeviceManager)
                context.getSystemService(EnterpriseDeviceManager.ENTERPRISE_POLICY_SERVICE);
        contentBlockProvider = new ServerContentBlockProvider();
        mFirewall = mEnterpriseDeviceManager.getFirewall();
    }

    @Override
    public boolean enableBlocker() {
        FirewallRule[] denyRuleArray = loadDenyArray();
        FirewallResponse[] firewallResponseArray = mFirewall.addRules(denyRuleArray);
        FirewallResponse enableFilrewallResponse = mFirewall.enableFirewall(true);
        if (enableFilrewallResponse.getResult() == FirewallResponse.Result.SUCCESS
                || enableFilrewallResponse.getResult() == FirewallResponse.Result.NO_CHANGES) {
            return true;
        }
        return false;

    }

    @Override
    public boolean disableBlocker() {
        mFirewall.clearRules(Firewall.FIREWALL_ALL_RULES);
        FirewallResponse firewallResponse = mFirewall.enableFirewall(false);
        if (firewallResponse.getResult() == FirewallResponse.Result.SUCCESS
                || firewallResponse.getResult() == FirewallResponse.Result.NO_CHANGES) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isEnabled() {
        return mFirewall.isFirewallEnabled();
    }

    private FirewallRule[] loadDenyArray() {
        List<String> urls = contentBlockProvider.loadBlockDb().urlsToBlock;
        FirewallRule[] denyRuleArray = new FirewallRule[URL_BLOCK_LIMIT];
        for (int i = 0; i < urls.size(); i++) {
            FirewallRule denyRule = new FirewallRule(FirewallRule.RuleType.DENY, Firewall.AddressType.IPV4);
            denyRule.setIpAddress(urls.get(i));
            denyRule.setPortLocation(Firewall.PortLocation.ALL);
            denyRule.setPackageName("*");
            denyRule.setNetworkInterface(Firewall.NetworkInterface.ALL_NETWORKS);
            denyRule.setDirection(Firewall.Direction.ALL);
            denyRule.setProtocol(Firewall.Protocol.ALL);
            denyRuleArray[i] = denyRule;
            if (i == URL_BLOCK_LIMIT) {
                break;
            }
        }
        return denyRuleArray;
    }
}
