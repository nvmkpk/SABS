package com.getadhell.androidapp.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.getadhell.androidapp.R;

public class BlockedUrlSettingFragment extends Fragment {
    private static final String TAG = BlockedUrlSettingFragment.class.getCanonicalName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_blocked_url_settings, container, false);
        Button seeStandardPackageButton = (Button) view.findViewById(R.id.seeStandardPackageButton);
        seeStandardPackageButton.setOnClickListener(v -> {
            Log.d(TAG, "Edit button click in Fragment1");
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, new WhiteListFragment());
            fragmentTransaction.addToBackStack("manage_url_to_manage_standard");
            fragmentTransaction.commit();
        });

        Button addCustomBlockedUrl = (Button) view.findViewById(R.id.addCustomBlockedUrl);
        addCustomBlockedUrl.setOnClickListener(v -> {
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, new BlockCustomUrlFragment());
            fragmentTransaction.addToBackStack("manage_url_to_add_custom");
            fragmentTransaction.commit();
        });

        Button showCustomUrlProvidersFragmentButton = (Button) view.findViewById(R.id.showCustomUrlProvidersFragmentButton);
        showCustomUrlProvidersFragmentButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getFragmentManager();

            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, new CustomBlockUrlProviderFragment());
            fragmentTransaction.addToBackStack("manage_custom_url_providers");
            fragmentTransaction.commit();
        });
        return view;
    }
}
