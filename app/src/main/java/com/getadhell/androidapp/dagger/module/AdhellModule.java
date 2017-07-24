package com.getadhell.androidapp.dagger.module;

import android.app.admin.DevicePolicyManager;
import android.app.enterprise.ApplicationPolicy;
import android.app.enterprise.EnterpriseDeviceManager;
import android.app.enterprise.license.EnterpriseLicenseManager;
import android.content.ComponentName;
import android.content.Context;

import com.getadhell.androidapp.utils.DeviceAdminInteractor;

import dagger.Module;

@Module
public class AdhellModule {

    DeviceAdminInteractor providesDeviceAdminInteractor(EnterpriseDeviceManager enterpriseDeviceManager,
                                                        DevicePolicyManager devicePolicyManager,
                                                        ApplicationPolicy mApplicationPolicy,
                                                        Context mContext,
                                                        ComponentName componentName,
                                                        EnterpriseLicenseManager enterpriseLicenseManager) {


        return null;
    }

}
