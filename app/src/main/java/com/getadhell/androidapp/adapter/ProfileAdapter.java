package com.getadhell.androidapp.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.getadhell.androidapp.R;
import com.getadhell.androidapp.db.entity.PolicyPackage;

import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private List<PolicyPackage> policyPackages;

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ProfileViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class ProfileViewHolder extends RecyclerView.ViewHolder {
        TextView profileNameTextView;
        Switch profileSwitch;
        TextView lastModifiedTextView;
        TextView numberOfDisabledPackagesTextView;
        TextView numberOfHostsTextView;
        TextView numberOfUserBlockedUrlsTextView;
        TextView numberOfUserWhitelistUrlsTextView;
        TextView numberOfRestrictedPermissions;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            profileNameTextView = itemView.findViewById(R.id.profileNameTextView);
            profileSwitch = itemView.findViewById(R.id.profileSwitch);
            lastModifiedTextView = itemView.findViewById(R.id.lastModifiedTextView);
            numberOfDisabledPackagesTextView = itemView.findViewById(R.id.numberOfDisabledPackagesTextView);
            numberOfHostsTextView = itemView.findViewById(R.id.numberOfHostsTextView);
            numberOfUserBlockedUrlsTextView = itemView.findViewById(R.id.numberOfUserBlockedUrlsTextView);
            numberOfUserWhitelistUrlsTextView = itemView.findViewById(R.id.numberOfUserWhitelistUrlsTextView);
            numberOfRestrictedPermissions = itemView.findViewById(R.id.numberOfRestrictedPermissions);
        }
    }
}
