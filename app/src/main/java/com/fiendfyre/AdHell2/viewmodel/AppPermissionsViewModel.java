package com.fiendfyre.AdHell2.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;

import com.fiendfyre.AdHell2.db.AppDatabase;

import java.util.List;

public class AppPermissionsViewModel extends AndroidViewModel {
    private AppDatabase mAppDatabase;
    private List<String> permissions;

    public AppPermissionsViewModel(Application application) {
        super(application);
        mAppDatabase = AppDatabase.getAppDatabase(application);
    }
}
