package com.getadhell.androidapp.adapter;

import android.content.Context;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.getadhell.androidapp.R;
import com.getadhell.androidapp.db.entity.AppInfo;

import java.util.List;


public class AppWhitelistAdapter extends ArrayAdapter<AppInfo> {
    private static final String TAG = AppWhitelistAdapter.class.getCanonicalName();
    private PackageManager mPackageManager;

    public AppWhitelistAdapter(@NonNull Context context, @NonNull List<AppInfo> objects) {
        super(context, 0, objects);
        mPackageManager = getContext().getPackageManager();
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_app_list_view, parent, false);
        }
        AppInfo appInfo = getItem(position);
        if (appInfo != null) {
            ImageView appIconImageView = (ImageView) convertView.findViewById(R.id.appIconImageView);
            TextView appNameTextView = (TextView) convertView.findViewById(R.id.appNameTextView);
            Switch adhellWhitelistAppSwitch = (Switch) convertView.findViewById(R.id.adhellWhitelistAppSwitch);
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
}
