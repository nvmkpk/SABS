package com.getadhell.androidapp.fragments;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.getadhell.androidapp.model.AdhellPermissionInfo;

public class SharedAppPermissionViewModel extends ViewModel {
    private final MutableLiveData<AdhellPermissionInfo> selected = new MutableLiveData<AdhellPermissionInfo>();

    public void select(AdhellPermissionInfo adhellPermissionInfo) {
        selected.setValue(adhellPermissionInfo);
    }

    public LiveData<AdhellPermissionInfo> getSelected() {
        return selected;
    }

}
