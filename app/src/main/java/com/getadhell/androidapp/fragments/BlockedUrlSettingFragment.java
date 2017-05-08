package com.getadhell.androidapp.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
        seeStandardPackageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Edit button click in Fragment1");
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, new BlockListFragment());
                fragmentTransaction.addToBackStack("manage_url_to_manage_standard");
                fragmentTransaction.commit();
            }

        });

        Button addCustomBlockedUrl = (Button) view.findViewById(R.id.addCustomBlockedUrl);
        addCustomBlockedUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, new BlockCustomUrlFragment());
                fragmentTransaction.addToBackStack("manage_url_to_add_custom");
                fragmentTransaction.commit();
            }
        });
        return view;
    }
}
