package com.getadhell.androidapp.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.getadhell.androidapp.R;
import com.getadhell.androidapp.adapter.AppWhitelistAdapter;
import com.getadhell.androidapp.db.entity.AppInfo;
import com.getadhell.androidapp.viewmodel.AdhellWhitelistAppsViewModel;


public class AppListFragment extends LifecycleFragment {
    private static final String TAG = AppListFragment.class.getCanonicalName();
    private ListView appListView;
    private AppWhitelistAdapter appWhitelistAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_list, container, false);
        appListView = view.findViewById(R.id.appList);
        this.getActivity().setTitle(R.string.adblock_whitelist);

        AdhellWhitelistAppsViewModel adhellWhitelistAppsViewModel = ViewModelProviders.of(getActivity()).get(AdhellWhitelistAppsViewModel.class);
        adhellWhitelistAppsViewModel.getSortedAppInfo().observe(this, appInfos -> {
            if (appWhitelistAdapter == null) {
                appWhitelistAdapter = new AppWhitelistAdapter(this.getContext(), appInfos);
                appListView.setAdapter(appWhitelistAdapter);
            } else {
                appWhitelistAdapter.notifyDataSetChanged();
            }
        });

        appListView.setOnItemClickListener((parent, view1, position, id) -> {
            AppInfo appInfo = (AppInfo) parent.getItemAtPosition(position);
            Log.d(TAG, "Will toggle app: " + appInfo.packageName);
            adhellWhitelistAppsViewModel.toggleApp(appInfo);
        });
        return view;
    }
}
