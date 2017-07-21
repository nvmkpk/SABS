package com.getadhell.androidapp.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getadhell.androidapp.R;

public class AdhellPermissionInAppsFragment extends LifecycleFragment {
    RecyclerView permissionInAppsRecyclerView;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_permission_in_apps, container, false);
        permissionInAppsRecyclerView = (RecyclerView) view.findViewById(R.id.permissionInAppsRecyclerView);

        return null;
    }
}
