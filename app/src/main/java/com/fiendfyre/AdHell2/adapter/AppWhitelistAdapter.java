package com.fiendfyre.AdHell2.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.fiendfyre.AdHell2.R;
import com.fiendfyre.AdHell2.db.entity.AppInfo;

import java.util.ArrayList;
import java.util.List;


public class AppWhitelistAdapter extends ArrayAdapter<AppInfo> {
    private static final String TAG = AppWhitelistAdapter.class.getCanonicalName();
    private List<AppInfo> appInfos;
    private List<AppInfo> mAppInfoOriginal;
    private PackageManager mPackageManager;

    public AppWhitelistAdapter(@NonNull Context context, @NonNull List<AppInfo> appInfos) {
        super(context, 0, appInfos);
        mPackageManager = getContext().getPackageManager();
        this.appInfos = appInfos;
        mAppInfoOriginal = appInfos;
    }

    @Override
    public int getCount() {
        if (appInfos == null) {
            return 0;
        }
        return appInfos.size();
    }

    @Nullable
    @Override
    public AppInfo getItem(int position) {
        return appInfos.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_app_list_view, parent, false);
        }
        AppInfo appInfo = getItem(position);
        if (appInfo != null) {
            ImageView appIconImageView = convertView.findViewById(R.id.appIconImageView);
            TextView appNameTextView = convertView.findViewById(R.id.appNameTextView);
            Switch adhellWhitelistAppSwitch = convertView.findViewById(R.id.adhellWhitelistAppSwitch);
            appNameTextView.setText(appInfo.appName);
            try {
                appIconImageView.setImageDrawable(mPackageManager.getApplicationIcon(appInfo.packageName));
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Failed to get application icon", e);
            }
            adhellWhitelistAppSwitch.setChecked(!appInfo.adhellWhitelisted);
        }
        return convertView;
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return new Filter() {

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count == 0) {
                    notifyDataSetInvalidated();
                } else {
                    appInfos = (List<AppInfo>) results.values;
                    notifyDataSetChanged();
                }
            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                Log.d(TAG, "Performing filtering");
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    ArrayList<AppInfo> list = new ArrayList<>(mAppInfoOriginal);
                    results.values = list;
                    results.count = list.size();
                } else {
                    List<AppInfo> filteredAppInfos = new ArrayList<>();
                    for (AppInfo info : mAppInfoOriginal) {
                        if (info.packageName.contains(constraint.toString().toLowerCase())
                                || info.appName.toLowerCase().contains(constraint.toString().toLowerCase())) {
                            filteredAppInfos.add(info);
                            Log.d(TAG, "appInfo: " + info.appName + " " + info.packageName);
                        }
                    }
                    Log.d(TAG, "Number of filtered apps: " + filteredAppInfos.size() + " filter: " + constraint);
                    results.values = filteredAppInfos;
                    results.count = filteredAppInfos.size();
                }
                return results;
            }
        };
    }
}
