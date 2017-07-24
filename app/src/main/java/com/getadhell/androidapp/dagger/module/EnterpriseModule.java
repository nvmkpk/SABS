package com.getadhell.androidapp.dagger.module;

import android.app.enterprise.ApplicationPermissionControlPolicy;
import android.app.enterprise.ApplicationPolicy;
import android.app.enterprise.EnterpriseDeviceManager;
import android.app.enterprise.FirewallPolicy;
import android.app.enterprise.license.EnterpriseLicenseManager;
import android.content.Context;
import android.util.Log;

import com.getadhell.androidapp.dagger.scope.AdhellApplicationScope;
import com.sec.enterprise.firewall.Firewall;

import dagger.Module;
import dagger.Provides;

@Module(includes = {AppModule.class})
public class EnterpriseModule {
    private static final String TAG = EnterpriseModule.class.getCanonicalName();

    @Provides
    @AdhellApplicationScope
    EnterpriseLicenseManager providesEnterpriseLicenseManager(Context appContext) {
        try {
            Log.i(TAG, "Trying to get EnterpriseLicenseManager");
            return EnterpriseLicenseManager.getInstance(appContext);
        } catch (Throwable e) {
            Log.e(TAG, "Failed to get EnterpriseLicenseManager. So it seems that Knox is not supported on this device", e);
        }
        return null;
    }

    @Provides
    @AdhellApplicationScope
    EnterpriseDeviceManager providesEnterpriseDeviceManager(Context appContext) {
        try {
            Log.i(TAG, "Trying to get EnterpriseDeviceManager");
            return (EnterpriseDeviceManager) appContext.getSystemService(EnterpriseDeviceManager.ENTERPRISE_POLICY_SERVICE);
        } catch (Throwable e) {
            Log.w(TAG, "Failed to get EnterpriseDeviceManager", e);
            return null;
        }
    }

    @Provides
    @AdhellApplicationScope
    ApplicationPolicy providesApplicationPolicy(EnterpriseDeviceManager enterpriseDeviceManager) {
        if (enterpriseDeviceManager == null) {
            return null;
        }
        return enterpriseDeviceManager.getApplicationPolicy();
    }

    @Provides
    @AdhellApplicationScope
    ApplicationPermissionControlPolicy providesApplicationPermissionControlPolicy(EnterpriseDeviceManager enterpriseDeviceManager) {
        if (enterpriseDeviceManager == null) {
            Log.w(TAG, "enterpriseDeviceManager is null. Can't get ApplicationPermissionControlPolicy");
            return null;
        }
        return enterpriseDeviceManager.getApplicationPermissionControlPolicy();
    }

    @Provides
    @AdhellApplicationScope
    FirewallPolicy providesFirewallPolicy(EnterpriseDeviceManager enterpriseDeviceManager) {
        if (enterpriseDeviceManager == null) {
            Log.w(TAG, "enterpriseDeviceManager is null. Can't get FirewallPolicy");
        }
        try {
            Log.i(TAG, "Trying to get FirewallPolicy");
            return enterpriseDeviceManager.getFirewallPolicy();
        } catch (Throwable e) {
            Log.e(TAG, "Failed to get firewallPolicy", e);
        }
        return null;
    }

    @Provides
    @AdhellApplicationScope
    Firewall providesFirewall(EnterpriseDeviceManager enterpriseDeviceManager) {
        if (enterpriseDeviceManager == null) {
            Log.w(TAG, "enterpriseDeviceManager is null. Can't get firewall");
        }
        try {
            Log.i(TAG, "Trying to get Firewall");
            return enterpriseDeviceManager.getFirewall();
        } catch (Throwable throwable) {
            Log.e(TAG, "Failed to get firewall", throwable);
        }
        return null;
    }

}
