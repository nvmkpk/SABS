package com.getadhell.androidapp.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getadhell.androidapp.R;
import com.getadhell.androidapp.adapter.AdhellPermissionInfoAdapter;
import com.getadhell.androidapp.model.AdhellPermissionInfo;

import java.util.List;

public class AdhellPermissionInfoFragment extends LifecycleFragment {
    private RecyclerView permissionInfoRecyclerView;
    private List<AdhellPermissionInfo> adhellPermissionInfos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        adhellPermissionInfos = AdhellPermissionInfo.loadPermissions();
        View view = inflater.inflate(R.layout.fragment_adhell_permission_info, container, false);
        permissionInfoRecyclerView = (RecyclerView) view.findViewById(R.id.permissionInfoRecyclerView);
        AdhellPermissionInfoAdapter adhellPermissionInfoAdapter = new AdhellPermissionInfoAdapter(this.getContext(), adhellPermissionInfos);
        permissionInfoRecyclerView.setAdapter(adhellPermissionInfoAdapter);
        permissionInfoRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this.getContext(), DividerItemDecoration.VERTICAL);
        permissionInfoRecyclerView.addItemDecoration(itemDecoration);
        return view;
    }
}
