package com.getadhell.androidapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.getadhell.androidapp.deviceadmin.DeviceAdminInteractor;
import com.getadhell.androidapp.fragments.ActivateKnoxLicenseFragment;
import com.getadhell.androidapp.fragments.AdhellNotSupportedFragment;
import com.getadhell.androidapp.fragments.BlockerFragment;
import com.getadhell.androidapp.fragments.EnableAdminFragment;
import com.getadhell.androidapp.fragments.NoInternetFragment;
import com.getadhell.androidapp.utils.DeviceUtils;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getCanonicalName();

    private static FragmentManager fragmentManager;
    private DeviceAdminInteractor mAdminInteractor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Answers(), new Crashlytics());
        setContentView(R.layout.activity_main);
        fragmentManager = getFragmentManager();
        mAdminInteractor = new DeviceAdminInteractor(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!DeviceUtils.isContentBlockerSupported(this)) {
            Log.i(LOG_TAG, "Device not supported");
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, new AdhellNotSupportedFragment());
            fragmentTransaction.commit();
            return;
        }
        if (!mAdminInteractor.isActiveAdmin()) {
            Log.d(LOG_TAG, "Admin is not active. Request enabling");
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, new EnableAdminFragment());
            fragmentTransaction.commit();
            return;
        }
        if (!mAdminInteractor.isKnoxEnbaled()) {
            Log.d(LOG_TAG, "Knox disabled");
            Log.d(LOG_TAG, "Checking if internet connection exists");
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            Log.d(LOG_TAG, "Is internet connection exists: " + isConnected);
            if (!isConnected) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, new NoInternetFragment());
                fragmentTransaction.commit();
                return;
            }
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, new ActivateKnoxLicenseFragment());
            fragmentTransaction.commit();
            return;
        }
        Log.d(LOG_TAG, "Everything is okay");
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, new BlockerFragment());
        fragmentTransaction.commit();
    }


    /**
     * Change permissions of phone. Allow or restrict
     *
     * @param view button
     */

    public void enableAdmin(View view) {
        mAdminInteractor.forceEnableAdmin();
    }
}
