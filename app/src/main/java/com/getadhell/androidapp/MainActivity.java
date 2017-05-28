package com.getadhell.androidapp;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.getadhell.androidapp.blocker.ContentBlocker;
import com.getadhell.androidapp.blocker.ContentBlocker56;
import com.getadhell.androidapp.blocker.ContentBlocker57;
import com.getadhell.androidapp.deviceadmin.DeviceAdminInteractor;
import com.getadhell.androidapp.dialogfragment.AdhellNotSupportedDialogFragment;
import com.getadhell.androidapp.dialogfragment.AdhellTurnOnDialogFragment;
import com.getadhell.androidapp.dialogfragment.NoInternetConnectionDialogFragment;
import com.getadhell.androidapp.fragments.AdhellNotSupportedFragment;
import com.getadhell.androidapp.fragments.BlockerFragment;
import com.getadhell.androidapp.service.BlockedDomainService;
import com.getadhell.androidapp.utils.AppWhiteList;
import com.getadhell.androidapp.utils.DeviceUtils;
import com.roughike.bottombar.BottomBar;

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

        BottomBar bottomBar = (BottomBar) findViewById(R.id.bottomBar);

        bottomBar.setOnTabSelectListener(tabId -> {
            switch (tabId) {
                case R.id.blockerTab:
                    Toast.makeText(getApplicationContext(), "Adhell tab", Toast.LENGTH_LONG).show();
                    break;
                case R.id.packageDisablerTab:
                    Toast.makeText(getApplicationContext(), "Package Disabler tab", Toast.LENGTH_LONG).show();
                    break;
                case R.id.appSupportTab:
                    Toast.makeText(getApplicationContext(), "Support tab", Toast.LENGTH_LONG).show();
            }
        });

        fragmentManager = getFragmentManager();
        if (!DeviceUtils.isContentBlockerSupported()) {
            return;
        }
        mAdminInteractor = DeviceAdminInteractor.getInstance();
        AppWhiteList appWhiteList = new AppWhiteList();
        appWhiteList.addToWhiteList("com.google.android.music");
//        HeartbeatAlarmHelper.scheduleAlarm();
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
        showDialog();
        if (!DeviceUtils.isContentBlockerSupported()) {
            Log.i(TAG, "Device not supported");
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, new AdhellNotSupportedFragment());
            fragmentTransaction.commit();
            return;
        }
        Log.d(TAG, "Everything is okay");
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, new BlockerFragment());
        fragmentTransaction.commitAllowingStateLoss();

        ContentBlocker contentBlocker = DeviceUtils.getContentBlocker();
        if (contentBlocker != null && contentBlocker.isEnabled() && (contentBlocker instanceof ContentBlocker56
                || contentBlocker instanceof ContentBlocker57)) {
            Intent i = new Intent(App.get().getApplicationContext(), BlockedDomainService.class);
            i.putExtra("launchedFrom", "main-activity");
            App.get().getApplicationContext().startService(i);
        }
    }


    /**
     * Change permissions of phone. Allow or restrict
     *
     * @param view button
     */

    public void enableAdmin(View view) {
        mAdminInteractor.forceEnableAdmin(this);
    }

    public void showDialog() {
        if (!(DeviceUtils.isSamsung() && DeviceUtils.isKnoxSupported())) {
            Log.i(TAG, "Device not supported");
            AdhellNotSupportedDialogFragment adhellNotSupportedDialogFragment = AdhellNotSupportedDialogFragment.newInstance("Some title");
            adhellNotSupportedDialogFragment.show(fragmentManager, "dialog_fragment_adhell_not_supported");
            adhellNotSupportedDialogFragment.setCancelable(false);
            return;
        }
        if (!mAdminInteractor.isActiveAdmin()) {
            Log.d(TAG, "Admin is not active. Request enabling");
            AdhellTurnOnDialogFragment adhellTurnOnDialogFragment = AdhellTurnOnDialogFragment.newInstance("Adhell Turn On");
            adhellTurnOnDialogFragment.show(fragmentManager, "dialog_fragment_turn_on_adhell");
            adhellTurnOnDialogFragment.setCancelable(false);
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
                NoInternetConnectionDialogFragment noInternetConnectionDialogFragment = NoInternetConnectionDialogFragment.newInstance("No Internet connection");
                noInternetConnectionDialogFragment.show(fragmentManager, "dialog_fragment_no_internet_connection");
                noInternetConnectionDialogFragment.setCancelable(false);
            }
            AdhellTurnOnDialogFragment adhellTurnOnDialogFragment = AdhellTurnOnDialogFragment.newInstance("Adhell Turn On");
            adhellTurnOnDialogFragment.show(fragmentManager, "dialog_fragment_turn_on_adhell");
            adhellTurnOnDialogFragment.setCancelable(false);
        }
    }
}
