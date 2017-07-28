package com.getadhell.androidapp.dagger.component;

import com.getadhell.androidapp.MainActivity;
import com.getadhell.androidapp.adapter.AdhellPermissionInAppsAdapter;
import com.getadhell.androidapp.blocker.ContentBlocker20;
import com.getadhell.androidapp.blocker.ContentBlocker56;
import com.getadhell.androidapp.dagger.module.AdhellModule;
import com.getadhell.androidapp.dagger.module.AdminModule;
import com.getadhell.androidapp.dagger.module.AppModule;
import com.getadhell.androidapp.dagger.module.EnterpriseModule;
import com.getadhell.androidapp.dagger.module.NetworkModule;
import com.getadhell.androidapp.dagger.scope.AdhellApplicationScope;
import com.getadhell.androidapp.fragments.BlockedUrlSettingFragment;
import com.getadhell.androidapp.fragments.PackageDisablerFragment;
import com.getadhell.androidapp.service.BlockedDomainService;
import com.getadhell.androidapp.utils.AppsListDBInitializer;
import com.getadhell.androidapp.utils.DeviceAdminInteractor;
import com.getadhell.androidapp.viewmodel.AdhellWhitelistAppsViewModel;
import com.getadhell.androidapp.viewmodel.SharedAppPermissionViewModel;

import dagger.Component;

@AdhellApplicationScope
@Component(modules = {AppModule.class, AdminModule.class, EnterpriseModule.class, AdhellModule.class, NetworkModule.class})
public interface AppComponent {
    void inject(DeviceAdminInteractor deviceAdminInteractor);

    void inject(ContentBlocker56 contentBlocker56);

    void inject(ContentBlocker20 contentBlocker20);

    void inject(BlockedDomainService blockedDomainService);

    void inject(BlockedUrlSettingFragment blockedUrlSettingFragment);

    void inject(PackageDisablerFragment packageDisablerFragment);

    void inject(AdhellWhitelistAppsViewModel adhellWhitelistAppsViewModel);

    void inject(MainActivity mainActivity);

    void inject(SharedAppPermissionViewModel sharedAppPermissionViewModel);

    void inject(AdhellPermissionInAppsAdapter adhellPermissionInAppsAdapter);

    void inject(AppsListDBInitializer appsListDBInitializer);
}
