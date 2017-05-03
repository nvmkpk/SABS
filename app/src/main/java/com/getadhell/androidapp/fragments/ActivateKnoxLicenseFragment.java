package com.getadhell.androidapp.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.getadhell.androidapp.R;
import com.getadhell.androidapp.deviceadmin.DeviceAdminInteractor;
import com.getadhell.androidapp.utils.DeviceUtils;

public class ActivateKnoxLicenseFragment extends Fragment {
    private static final String LOG_TAG = ActivateKnoxLicenseFragment.class.getCanonicalName();
    private static Context context;
    private static FragmentManager fragmentManager;
    private static Button mActivateKnoxButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.knox_license_activation_fragment, container, false);
        context = this.getActivity();
        fragmentManager = this.getFragmentManager();
        mActivateKnoxButton = (Button) view.findViewById(R.id.activateKnoxButton);
        mActivateKnoxButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(LOG_TAG, "Button click in Fragment1");
                activateKnoxLicense();
            }
        });
        return view;
    }

    private void activateKnoxLicense() {
        new ActivateKnoxLicenseTask().execute(false);
    }

    public static class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            DeviceAdminInteractor deviceAdminInteractor = new DeviceAdminInteractor();
            if (deviceAdminInteractor.isKnoxEnbaled()) {
                Toast.makeText(context, "License activated", Toast.LENGTH_LONG).show();
                Log.d(LOG_TAG, "License activated");
            } else {
                Toast.makeText(context, "License activation failed", Toast.LENGTH_LONG).show();
                Log.w(LOG_TAG, "License activation failed");
            }
            if (DeviceUtils.isContentBlockerSupported()
                    && deviceAdminInteractor.isKnoxEnbaled()) {
                if (fragmentManager != null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, new BlockerFragment());
                    fragmentTransaction.commit();
                    return;
                }
            }
            if (fragmentManager != null) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, new KnoxActivationFailedFragment());
                fragmentTransaction.commit();
            }

        }
    }

    private class ActivateKnoxLicenseTask extends AsyncTask<Boolean, Void, Integer> {
        protected void onPreExecute() {
            if (mActivateKnoxButton != null) {
                mActivateKnoxButton.setEnabled(false);
                mActivateKnoxButton.setText(R.string.activating_knox_license);
            }
        }

        protected Integer doInBackground(Boolean... switchers) {
            try {
                DeviceAdminInteractor deviceAdminInteractor = new DeviceAdminInteractor();
                deviceAdminInteractor.forceActivateKnox();
                if (deviceAdminInteractor.isKnoxEnbaled()) {
                    return 1;
                }
                return 0;
            } catch (Exception e) {
                Log.e(LOG_TAG, "Failed to activate Knox license", e);
            }
            return 0;
        }

        protected void onPostExecute(Integer result) {

//            if (mActivateKnoxButton != null) {
//                mActivateKnoxButton.setText(R.string.knox_license_activated);
//                mActivateKnoxButton.setEnabled(true);
//            }
        }
    }
}
