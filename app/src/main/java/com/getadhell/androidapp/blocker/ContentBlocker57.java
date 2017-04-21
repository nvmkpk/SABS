package com.getadhell.androidapp.blocker;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.sec.enterprise.AppIdentity;
import com.sec.enterprise.firewall.DomainFilterRule;
import com.sec.enterprise.firewall.Firewall;

import java.util.ArrayList;
import java.util.List;

public class ContentBlocker57 extends ContentBlocker56 {
    private static final String TAG = ContentBlocker57.class.getCanonicalName();
    Firewall mFirewall;
    Context mContext;

    public ContentBlocker57(Context context) {
        super(context);
        mFirewall = this.getmFirewall();
        mContext = context;
    }

    @Override
    public boolean enableBlocker() {
        if (super.enableBlocker()) {
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("dnsAddresses", Context.MODE_PRIVATE);
            if (sharedPreferences.contains("dns1") && sharedPreferences.contains("dns2")) {
                String dns1 = sharedPreferences.getString("dns1", "8.8.8.8");
                String dns2 = sharedPreferences.getString("dns2", "8.8.4.4");
                this.setDns(dns1, dns2);
                Log.d(TAG, "Previous dns addresses has been applied. " + dns1 + " " + dns2);
            }
            return true;
        }
        return false;
    }

    public void setDns(String dns1, String dns2) {
        DomainFilterRule domainFilterRule = new DomainFilterRule(new AppIdentity(Firewall.FIREWALL_ALL_PACKAGES, null));
        domainFilterRule.setDns1(dns1);
        domainFilterRule.setDns2(dns2);
        List<DomainFilterRule> rules = new ArrayList<>();
        rules.add(domainFilterRule);
        mFirewall.addDomainFilterRules(rules);
        Log.d(TAG, "DNS1: " + domainFilterRule.getDns1());
        Log.d(TAG, "DNS2: " + domainFilterRule.getDns2());
    }

    public String getStringDns() {
        DomainFilterRule domainFilterRule = new DomainFilterRule(new AppIdentity(Firewall.FIREWALL_ALL_PACKAGES, null));
        String dns = domainFilterRule.getDns1() + " " + domainFilterRule.getDns2();
        return dns;
    }
}
