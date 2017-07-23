package com.getadhell.androidapp.adapter;

import android.app.enterprise.ApplicationPermissionControlPolicy;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.getadhell.androidapp.R;
import com.getadhell.androidapp.db.entity.AppInfo;
import com.getadhell.androidapp.fragments.SharedAppPermissionViewModel;
import com.getadhell.androidapp.utils.DeviceUtils;

import java.util.List;

public class AdhellPermissionInAppsAdapter extends RecyclerView.Adapter<AdhellPermissionInAppsAdapter.ViewHolder> {
    private Context mContext;
    private List<AppInfo> appInfos;
    private PackageManager mPackageManager;
    private ApplicationPermissionControlPolicy mAppControlPolicy;
    private SharedAppPermissionViewModel sharedAppPermissionViewModel;


    public AdhellPermissionInAppsAdapter(Context context, List<AppInfo> packageInfos) {
        this.mContext = context;
        this.appInfos = packageInfos;
        this.mPackageManager = context.getPackageManager();
        this.mAppControlPolicy =
                DeviceUtils.getEnterpriseDeviceManager().getApplicationPermissionControlPolicy();
//        sharedAppPermissionViewModel = ViewModelProviders.of(context).get(SharedAppPermissionViewModel.class);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View permissionInApp = inflater.inflate(R.layout.item_permission_in_app, parent, false);
        return new AdhellPermissionInAppsAdapter.ViewHolder(permissionInApp);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        AppInfo appInfo = appInfos.get(position);
//        holder.appIconImageView = packageInfo.
        holder.appNameTextView.setText(appInfo.appName);
        holder.appPackageNameTextView.setText(appInfo.packageName);
//        holder.appPermissionSwitch.


    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        ImageView appIconImageView;
        TextView appNameTextView;
        TextView appPackageNameTextView;
        Switch appPermissionSwitch;

        ViewHolder(View itemView) {
            super(itemView);
            appIconImageView = (ImageView) itemView.findViewById(R.id.appIconImageView);
            appNameTextView = (TextView) itemView.findViewById(R.id.appNameTextView);
            appPackageNameTextView = (TextView) itemView.findViewById(R.id.appPackageNameTextView);
            appPermissionSwitch = (Switch) itemView.findViewById(R.id.appPermissionSwitch);
        }
    }
}
