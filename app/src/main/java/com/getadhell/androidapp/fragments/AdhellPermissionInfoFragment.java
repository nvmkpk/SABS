package com.getadhell.androidapp.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.getadhell.androidapp.R;
import com.getadhell.androidapp.adapter.AdhellPermissionInfoAdapter;
import com.getadhell.androidapp.adapter.ItemClickSupport;
import com.getadhell.androidapp.model.AdhellPermissionInfo;
import com.getadhell.androidapp.viewmodel.SharedAppPermissionViewModel;

import java.util.List;

public class AdhellPermissionInfoFragment extends LifecycleFragment {
    private static final String TAG = AdhellPermissionInfoFragment.class.getCanonicalName();
    private List<AdhellPermissionInfo> adhellPermissionInfos;

    private SharedAppPermissionViewModel sharedAppPermissionViewModel;
    private FragmentManager fragmentManager;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean isPermissionGranted = (this.getContext()
                .checkCallingOrSelfPermission("android.permission.sec.MDM_APP_PERMISSION_MGMT")
                == PackageManager.PERMISSION_GRANTED);
        if (isPermissionGranted) {
            Log.i(TAG, "Permission granted");
        } else {
            Log.w(TAG, "Permission for application permission policy is not granted");
            Toast.makeText(this.getContext(), "You need to re-enable admin to make this work", Toast.LENGTH_LONG).show();
            // TODO: if not show re-enable dialog
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // TODO: Check if premium
        getActivity().setTitle("App Permissions");
        sharedAppPermissionViewModel = ViewModelProviders.of(getActivity()).get(SharedAppPermissionViewModel.class);
        fragmentManager = getActivity().getSupportFragmentManager();
        adhellPermissionInfos = AdhellPermissionInfo.loadPermissions();
        View view = inflater.inflate(R.layout.fragment_adhell_permission_info, container, false);
        RecyclerView permissionInfoRecyclerView = (RecyclerView) view.findViewById(R.id.permissionInfoRecyclerView);
        AdhellPermissionInfoAdapter adhellPermissionInfoAdapter = new AdhellPermissionInfoAdapter(this.getContext(), adhellPermissionInfos);
        permissionInfoRecyclerView.setAdapter(adhellPermissionInfoAdapter);
        permissionInfoRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL);

        sharedAppPermissionViewModel.loadInstalledAppsLiveData().observe(this, installedApps -> {
            sharedAppPermissionViewModel.installedApps = installedApps;
        });


        permissionInfoRecyclerView.addItemDecoration(itemDecoration);
        ItemClickSupport.addTo(permissionInfoRecyclerView).setOnItemClickListener(
                (recyclerView, position, v) -> {
                    sharedAppPermissionViewModel.select(adhellPermissionInfos.get(position));
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, new AdhellPermissionInAppsFragment());
                    fragmentTransaction.addToBackStack("permissionsInfo_permissionsInApp");
                    fragmentTransaction.commit();
                }
        );
        return view;
    }
}
