package com.getadhell.androidapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.enterprise.EnterpriseDeviceManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.sec.enterprise.firewall.DomainFilterReport;
import com.sec.enterprise.firewall.Firewall;

import java.util.List;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getCanonicalName();


    private static FragmentManager fragmentManager;
    private DeviceAdminInteractor mAdminInteractor;

    @Override
    public void onBackPressed() {
        int count = fragmentManager.getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            fragmentManager.popBackStack();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Answers(), new Crashlytics());
        setContentView(R.layout.activity_main);
        fragmentManager = getFragmentManager();
        mAdminInteractor = new DeviceAdminInteractor(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                fragmentManager.popBackStack();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!DeviceUtils.isContentBlockerSupported(this)) {
            Log.i(TAG, "Device not supported");
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, new AdhellNotSupportedFragment());
            fragmentTransaction.commit();
            return;
        }
        if (!mAdminInteractor.isActiveAdmin()) {
            Log.d(TAG, "Admin is not active. Request enabling");
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, new EnableAdminFragment());
            fragmentTransaction.commit();
            return;
        }
        if (!mAdminInteractor.isKnoxEnbaled()) {
            Log.d(TAG, "Knox disabled");
            Log.d(TAG, "Checking if internet connection exists");
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            Log.d(TAG, "Is internet connection exists: " + isConnected);
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
        Log.d(TAG, "Everything is okay");
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

    public void testReport(View view) {
        EnterpriseDeviceManager edm = (EnterpriseDeviceManager)
                this.getSystemService(EnterpriseDeviceManager.ENTERPRISE_POLICY_SERVICE);
        Firewall firewall = edm.getFirewall();
        List<DomainFilterReport> list = firewall.getDomainFilterReport(null);
        Log.d(TAG, String.format("Before clear. Number of urls: %d", list.size()));
        list.clear();
        Log.d(TAG, String.format("after clear. Number of urls: %d", list.size()));
        list = firewall.getDomainFilterReport(null);
        Log.d(TAG, String.format("after clear and get. Number of urls: %d", list.size()));
        for (DomainFilterReport domainFilterReport : list) {
            Log.d(TAG, String.format("blocked urls: %s", domainFilterReport.getDomainUrl()));
            domainFilterReport.getPackageName();
        }
        Log.d(TAG, list.toString());
    }
}
