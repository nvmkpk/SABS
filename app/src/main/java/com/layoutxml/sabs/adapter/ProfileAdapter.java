package com.layoutxml.sabs.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Switch;
import android.widget.TextView;

import com.layoutxml.sabs.R;
import com.layoutxml.sabs.db.entity.PolicyPackage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    private List<PolicyPackage> policyPackages;
    private Context mContext;

    public ProfileAdapter(Context context, List<PolicyPackage> policyPackages) {
        this.mContext = context;
        this.policyPackages = policyPackages;
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(ProfileViewHolder holder, int position) {
        PolicyPackage policyPackage = policyPackages.get(position);
        holder.profileNameTextView.setText(policyPackage.name);
        holder.profileSwitch.setChecked(policyPackage.active);
        Date updatedAt = policyPackage.updatedAt;
        SimpleDateFormat dt = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
        String lastModifiedText
                = mContext.getString(R.string.last_modified).replace("{{lastModified}}", dt.format(updatedAt));
        holder.lastModifiedTextView.setText(lastModifiedText);
        String numberOfDisabledPackagesText
                = mContext.getString(R.string.number_packages_disabled).replace("{{numberOfDisabledPackages}}", "" + policyPackage.numberOfDisabledPackages);
        holder.numberOfDisabledPackagesTextView.setText(numberOfDisabledPackagesText);
        String numberOfHostsText
                = mContext.getString(R.string.number_of_hosts)
                .replace("{{numberOfHosts}}", "" + policyPackage.numberOfHosts);
        holder.numberOfHostsTextView.setText(numberOfHostsText);

        String numberOfUserBlockedDomainsText
                = mContext.getString(R.string.custom_domains_blocked)
                .replace("{{numberOfDomainsBlockedByUser}}", "" + policyPackage.numberOfUserBlockedDomains);
        holder.numberOfUserBlockedUrlsTextView.setText(numberOfUserBlockedDomainsText);
        String numberOfWhitelistedDomainsText
                = mContext.getString(R.string.number_of_domains_whitelisted)
                .replace("{{numberOfWhitelistedDomains}}", "" + policyPackage.numberOfUserWhitelistedDomains);
        holder.numberOfUserWhitelistUrlsTextView.setText(numberOfWhitelistedDomainsText);

        String numberOfWhitelistedPermissionsText
                = mContext.getString(R.string.number_of_permissions_restricted)
                .replace("{{numberOfRestrictedPermissions}}", "" + policyPackage.numberOfChangedPermissions);
        holder.numberOfRestrictedPermissions.setText(numberOfWhitelistedPermissionsText);
    }

    @Override
    public int getItemCount() {
        return policyPackages.size();
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
