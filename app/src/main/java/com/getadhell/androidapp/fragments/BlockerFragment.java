package com.getadhell.androidapp.fragments;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.getadhell.androidapp.R;
import com.getadhell.androidapp.blocker.ContentBlocker;
import com.getadhell.androidapp.blocker.ContentBlocker56;
import com.getadhell.androidapp.blocker.ContentBlocker57;
import com.getadhell.androidapp.utils.BlockedDomainAlarmHelper;
import com.getadhell.androidapp.utils.DeviceUtils;

public class BlockerFragment extends Fragment {
    private static final String TAG = BlockerFragment.class.getCanonicalName();
    private static Button mPolicyChangeButton;
    private static TextView isSupportedTextView;
    private ContentBlocker contentBlocker;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_app_settings:
                Log.d(TAG, "App setting action clicked");
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, new AppSettingsFragment());
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.blocker_fragment, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mPolicyChangeButton = (Button) view.findViewById(R.id.policyChangeButton);
        isSupportedTextView = (TextView) view.findViewById(R.id.isSupportedTextView);

        contentBlocker = DeviceUtils.getContentBlocker();

        if (contentBlocker.isEnabled()) {
            mPolicyChangeButton.setText(R.string.block_button_text_turn_off);
            isSupportedTextView.setText(R.string.block_enabled);
        } else {
            mPolicyChangeButton.setText(R.string.block_button_text_turn_on);
            isSupportedTextView.setText(R.string.block_disabled);
        }
        mPolicyChangeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Button click in Fragment1");
                changePermission();
            }
        });
        setHasOptionsMenu(true);
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
        Log.d(TAG, "Entering changePermission");
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
                    Log.d(TAG, "Policy enabled, trying to disable");
                    contentBlocker.disableBlocker();
                    if (contentBlocker instanceof ContentBlocker56
                            || contentBlocker instanceof ContentBlocker57) {
                        BlockedDomainAlarmHelper.cancelBlockedDomainAlarm();
                    }
                } else {
                    // Disabled. Enabling
                    Log.d(TAG, "Policy disabled, trying to enable");
                    contentBlocker.disableBlocker();
                    contentBlocker.enableBlocker();
                    if (contentBlocker instanceof ContentBlocker56
                            || contentBlocker instanceof ContentBlocker57) {
                        BlockedDomainAlarmHelper.scheduleBlockedDomainAlarm();
                    }
                }
            } catch (Exception e) {
                Log.e(TAG, "Failed to turn on ad blocker", e);
                contentBlocker.disableBlocker();
                if (contentBlocker instanceof ContentBlocker56
                        || contentBlocker instanceof ContentBlocker57) {
                    BlockedDomainAlarmHelper.cancelBlockedDomainAlarm();
                }
            }
            return 42;
        }

        protected void onPostExecute(Integer result) {
            Log.d(TAG, "Enterting onPostExecute() method");
            setNeededText();
            mPolicyChangeButton.setEnabled(true);
            Log.d(TAG, "Leaving onPostExecute() method");
        }
    }


}
