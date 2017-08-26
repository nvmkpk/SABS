package com.fiendfyre.AdHell2.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.fiendfyre.AdHell2.R;
import com.fiendfyre.AdHell2.adapter.AppWhitelistAdapter;
import com.fiendfyre.AdHell2.db.entity.AppInfo;
import com.fiendfyre.AdHell2.viewmodel.AdhellWhitelistAppsViewModel;


public class AppListFragment extends LifecycleFragment {
    private static final String TAG = AppListFragment.class.getCanonicalName();
    private ListView appListView;
    private AppWhitelistAdapter appWhitelistAdapter;
    private EditText adblockEnabledAppSearchEditText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.getActivity().setTitle(R.string.adblock_whitelist);
        View view = inflater.inflate(R.layout.fragment_app_list, container, false);
        appListView = view.findViewById(R.id.appList);
        adblockEnabledAppSearchEditText = view.findViewById(R.id.adblockEnabledAppSearchEditText);

        adblockEnabledAppSearchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                charSequence.toString()


            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (appWhitelistAdapter != null) {
                    appWhitelistAdapter.getFilter().filter(editable.toString());
                }
            }
        });

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
