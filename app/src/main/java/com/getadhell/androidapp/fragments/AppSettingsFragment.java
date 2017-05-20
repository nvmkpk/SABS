package com.getadhell.androidapp.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.getadhell.androidapp.R;
import com.getadhell.androidapp.blocker.ContentBlocker;
import com.getadhell.androidapp.blocker.ContentBlocker56;
import com.getadhell.androidapp.blocker.ContentBlocker57;
import com.getadhell.androidapp.receiver.CustomDeviceAdminReceiver;
import com.getadhell.androidapp.dialogfragment.DnsChangeDialogFragment;
import com.getadhell.androidapp.utils.DeviceUtils;

public class AppSettingsFragment extends Fragment {
    private static final String TAG = AppSettingsFragment.class.getCanonicalName();
    private ContentBlocker contentBlocker;
    private Context mContext;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_app_settings, container, false);
        mContext = this.getActivity().getApplicationContext();
        contentBlocker = DeviceUtils.getContentBlocker();

        Button editButton = (Button) view.findViewById(R.id.editUrls);
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Edit button click in Fragment1");
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, new BlockedUrlSettingFragment());
                fragmentTransaction.addToBackStack("main_to_editUrl");
                fragmentTransaction.commit();
            }

        });

        Button appButton = (Button) view.findViewById(R.id.allowApps);
        if (contentBlocker instanceof ContentBlocker56 || contentBlocker instanceof ContentBlocker57) {
            appButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d(TAG, "Allow Apps button click in Fragment1");
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, new AppListFragment());
                    fragmentTransaction.addToBackStack("main_to_editApp");
                    fragmentTransaction.commit();
                }

            });
        } else {
            appButton.setVisibility(View.GONE);
        }

        Button deleteAppButton = (Button) view.findViewById(R.id.deleteApp);
        deleteAppButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                new AlertDialog.Builder(v.getContext())
                        .setTitle("Delete app")
                        .setMessage("Do you really want to turn off and delete Adhell?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                contentBlocker.disableBlocker();
                                ComponentName devAdminReceiver = new ComponentName(mContext, CustomDeviceAdminReceiver.class);
                                DevicePolicyManager dpm = (DevicePolicyManager) mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
                                dpm.removeActiveAdmin(devAdminReceiver);
                                Intent intent = new Intent(Intent.ACTION_DELETE);
                                intent.setData(Uri.parse("package:com.getadhell.androidapp"));
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        Button changeDnsButton = (Button) view.findViewById(R.id.changeDnsButton);
        if (contentBlocker instanceof ContentBlocker57) {
            changeDnsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d(TAG, "Show dns change dialog");
                    FragmentManager fragmentManager = getFragmentManager();
                    DnsChangeDialogFragment dnsChangeDialogFragment = DnsChangeDialogFragment.newInstance("Some title");
                    dnsChangeDialogFragment.show(fragmentManager, "dialog_fragment_dns");
                }
            });
        } else {
            changeDnsButton.setVisibility(View.GONE);
        }


        return view;
    }
}
