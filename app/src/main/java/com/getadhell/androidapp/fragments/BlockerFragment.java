package com.getadhell.androidapp.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.admin.DeviceAdminReceiver;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.getadhell.androidapp.R;
import com.getadhell.androidapp.blocker.ContentBlocker;
import com.getadhell.androidapp.blocker.ContentBlocker56;
import com.getadhell.androidapp.utils.DeviceUtils;

public class BlockerFragment extends Fragment {
    private static final String LOG_TAG = BlockerFragment.class.getCanonicalName();
    private static Button mPolicyChangeButton;
    private static TextView isSupportedTextView;
    private ContentBlocker contentBlocker;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.blocker_fragment, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mContext = this.getActivity().getApplicationContext();

        mPolicyChangeButton = (Button) view.findViewById(R.id.policyChangeButton);
        isSupportedTextView = (TextView) view.findViewById(R.id.isSupportedTextView);

        contentBlocker = DeviceUtils.getContentBlocker(getActivity());

        if (contentBlocker.isEnabled()) {
            mPolicyChangeButton.setText(R.string.block_button_text_turn_off);
            isSupportedTextView.setText(R.string.block_enabled);
        } else {
            mPolicyChangeButton.setText(R.string.block_button_text_turn_on);
            isSupportedTextView.setText(R.string.block_disabled);
        }
        mPolicyChangeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(LOG_TAG, "Button click in Fragment1");
                changePermission();
            }
        });

        Button editButton = (Button) view.findViewById(R.id.editUrls);
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(LOG_TAG, "Edit button click in Fragment1");
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.setCustomAnimations(R.animator.enter_from_left, R.animator.exit_to_right, R.animator.enter_from_right, R.animator.exit_to_left);
                fragmentTransaction.replace(R.id.fragmentContainer, new BlockListFragment());
                fragmentTransaction.addToBackStack("main_to_editUrl");
                fragmentTransaction.commit();
            }

        });

        Button appButton = (Button) view.findViewById(R.id.allowApps);
        if (contentBlocker instanceof ContentBlocker56) {
            appButton.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Log.d(LOG_TAG, "Allow Apps button click in Fragment1");
                    FragmentManager fragmentManager = getFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.setCustomAnimations(R.animator.enter_from_left, R.animator.exit_to_right, R.animator.enter_from_right, R.animator.exit_to_left);
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
                                ComponentName devAdminReceiver = new ComponentName(mContext, DeviceAdminReceiver.class);
                                DevicePolicyManager dpm = (DevicePolicyManager) mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
                                dpm.removeActiveAdmin(devAdminReceiver);
                                Intent intent = new Intent(Intent.ACTION_DELETE);
                                intent.setData(Uri.parse("com.getadhell.androidapp"));
                                startActivity(intent);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        return view;
    }

    public void setNeededText() {
        if (contentBlocker.isEnabled()) {
            mPolicyChangeButton.setText(R.string.block_button_text_turn_off);
            isSupportedTextView.setText(R.string.block_enabled);
        } else {
            mPolicyChangeButton.setText(R.string.block_button_text_turn_on);
            isSupportedTextView.setText(R.string.block_disabled);
        }
    }

    private void changePermission() {
        Log.d(LOG_TAG, "Entering changePermission");
        new AdhellSwitchTask().execute(false);

    }

    private class AdhellSwitchTask extends AsyncTask<Boolean, Void, Integer> {

        protected void onPreExecute() {
            mPolicyChangeButton.setEnabled(false);

            if (!contentBlocker.isEnabled()) {
                mPolicyChangeButton.setText(R.string.block_button_text_enabling);
                isSupportedTextView.setText(getString(R.string.please_wait));
            } else {
                mPolicyChangeButton.setText(R.string.block_button_text_disabling);
                isSupportedTextView.setText(getString(R.string.wait_deleting));
            }
        }

        protected Integer doInBackground(Boolean... switchers) {
            try {
                if (contentBlocker.isEnabled()) {
                    // Enabled. Trying to disable
                    Log.d(LOG_TAG, "Policy enabled, trying to disable");
                    contentBlocker.disableBlocker();
                } else {
                    // Disabled. Enabling
                    Log.d(LOG_TAG, "Policy disabled, trying to enable");
                    contentBlocker.disableBlocker();
                    contentBlocker.enableBlocker();
                }
            } catch (Exception e) {
                Log.e(LOG_TAG, "Failed to turn on ad blocker", e);
                contentBlocker.disableBlocker();
            }
            return 42;
        }

        protected void onPostExecute(Integer result) {
            Log.d(LOG_TAG, "Enterting onPostExecute() method");
            setNeededText();
            mPolicyChangeButton.setEnabled(true);
            Log.d(LOG_TAG, "Leaving onPostExecute() method");
        }
    }


}
