package com.getadhell.androidapp.fragments;

import android.app.Fragment;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.getadhell.androidapp.R;
import com.getadhell.androidapp.blocker.ContentBlocker;
import com.getadhell.androidapp.utils.DeviceUtils;

public class BlockerFragment extends Fragment {
    private static final String LOG_TAG = BlockerFragment.class.getCanonicalName();
    private static Button mPolicyChangeButton;
    private static TextView isSupportedTextView;
    private ContentBlocker contentBlocker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.blocker_fragment, container, false);

        mPolicyChangeButton = (Button) view.findViewById(R.id.policyChangeButton);
        isSupportedTextView = (TextView) view.findViewById(R.id.isSupportedTextView);

        contentBlocker = DeviceUtils.getContentBlocker(getActivity());

        if (contentBlocker.isEnabled()) {
            mPolicyChangeButton.setText("Turn Off");
            isSupportedTextView.setText(R.string.block_enabled);
        } else {
            mPolicyChangeButton.setText("Turn On");
            isSupportedTextView.setText(R.string.block_disabled);
        }
        mPolicyChangeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(LOG_TAG, "Button click in Fragment1");
                changePermission();
            }
        });
        return view;
    }

    public void setNeededText() {
        if (contentBlocker.isEnabled()) {
            mPolicyChangeButton.setText("Turn Off");
            isSupportedTextView.setText(R.string.block_enabled);
        } else {
            mPolicyChangeButton.setText("Turn On");
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
                mPolicyChangeButton.setText("Enabling...");
                isSupportedTextView.setText(getString(R.string.please_wait));
            } else {
                mPolicyChangeButton.setText("Disabling...");
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
