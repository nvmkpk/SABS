package com.getadhell.androidapp.dagger.component;

import com.getadhell.androidapp.MainActivity;
import com.getadhell.androidapp.blocker.ContentBlocker;
import com.getadhell.androidapp.dagger.module.AdhellModule;
import com.getadhell.androidapp.dagger.module.AdminModule;
import com.getadhell.androidapp.dagger.module.AppModule;
import com.getadhell.androidapp.dagger.module.EnterpriseModule;
import com.getadhell.androidapp.dagger.module.NetworkModule;
import com.getadhell.androidapp.dagger.scope.AdhellApplicationScope;
import com.getadhell.androidapp.fragments.BlockedUrlSettingFragment;
import com.getadhell.androidapp.fragments.PackageDisablerFragment;
import com.getadhell.androidapp.service.BlockedDomainService;
import com.getadhell.androidapp.utils.DeviceAdminInteractor;
import com.getadhell.androidapp.viewmodel.AdhellWhitelistAppsViewModel;

import dagger.Component;

@AdhellApplicationScope
@Component(modules = {AppModule.class, EnterpriseModule.class, AdminModule.class, AdhellModule.class, NetworkModule.class})
public interface AppComponent {
    void inject(DeviceAdminInteractor deviceAdminInteractor);

    void inject(ContentBlocker contentBlocker);

    void inject(BlockedDomainService blockedDomainService);

    void inject(BlockedUrlSettingFragment blockedUrlSettingFragment);

    void inject(PackageDisablerFragment packageDisablerFragment);

    void inject(AdhellWhitelistAppsViewModel adhellWhitelistAppsViewModel);

    void inject(MainActivity mainActivity);
}
