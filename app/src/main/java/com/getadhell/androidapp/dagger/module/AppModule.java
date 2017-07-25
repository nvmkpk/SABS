package com.getadhell.androidapp.dagger.module;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;

import com.getadhell.androidapp.dagger.scope.AdhellApplicationScope;
import com.getadhell.androidapp.db.AppDatabase;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }

    @Provides
    @AdhellApplicationScope
    Application providesApplication() {
        return mApplication;
    }

    @Provides
    @AdhellApplicationScope
    Context providesContext() {
        return mApplication.getApplicationContext();
    }

    @Provides
    @AdhellApplicationScope
    AppDatabase providesAppDatabase() {
        return AppDatabase.getAppDatabase(mApplication.getApplicationContext());
    }

    @Provides
    @AdhellApplicationScope
    PackageManager providesPackageManager() {
        return mApplication.getPackageManager();
    }
}
