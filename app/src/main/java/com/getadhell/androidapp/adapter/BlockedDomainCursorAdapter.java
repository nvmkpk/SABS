package com.getadhell.androidapp.adapter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.getadhell.androidapp.R;

import java.text.SimpleDateFormat;
import java.util.Date;


public class BlockedDomainCursorAdapter extends CursorAdapter {
    public static final String TAG = BlockedDomainCursorAdapter.class.getCanonicalName();
    private PackageManager packageManager;

    public BlockedDomainCursorAdapter(Context context, Cursor c) {
        super(context, c);
        packageManager = context.getPackageManager();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_blocked_url_info, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView blockedDomainIdTextView = (TextView) view.findViewById(R.id.blockedDomainIdTextView);
        ImageView blockedDomainIconImageView = (ImageView) view.findViewById(R.id.blockedDomainIconImageView);
        TextView blockedDomainAppNameTextView = (TextView) view.findViewById(R.id.blockedDomainAppNameTextView);
        TextView blockedDomainTimeTextView = (TextView) view.findViewById(R.id.blockedDomainTimeTextView);
        TextView blockedDomainUrlTextView = (TextView) view.findViewById(R.id.blockedDomainUrlTextView);

        int id = cursor.getInt(cursor.getColumnIndex("_id"));
        String url = cursor.getString(cursor.getColumnIndex("url"));
        long blockTimestamp = cursor.getLong(cursor.getColumnIndex("blockTimestamp"));
        String packageName = cursor.getString(cursor.getColumnIndex("packageName"));
        Drawable icon = null;
        try {
            icon = packageManager.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to get application icon.", e);
//            icon =

        }


        ApplicationInfo ai;
        try {
            ai = packageManager.getApplicationInfo(packageName, 0);
        } catch (final PackageManager.NameNotFoundException e) {
            ai = null;
        }
        String applicationName = (String) (ai != null ? packageManager.getApplicationLabel(ai) : "(unknown)");


        blockedDomainIdTextView.setText(id + "");
        if (icon != null) {
            blockedDomainIconImageView.setImageDrawable(icon);
        }
        blockedDomainAppNameTextView.setText(applicationName);
        blockedDomainTimeTextView.setText(new SimpleDateFormat("HH:mm:ss").format(new Date(blockTimestamp * 1000)));
        blockedDomainUrlTextView.setText(url);
    }
}
