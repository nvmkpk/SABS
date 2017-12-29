package com.layoutxml.sabs;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.layoutxml.sabs.blocker.ContentBlocker;
import com.layoutxml.sabs.blocker.ContentBlocker56;
import com.layoutxml.sabs.blocker.ContentBlocker57;
import com.layoutxml.sabs.dialogfragment.AdhellNotSupportedDialogFragment;
import com.layoutxml.sabs.dialogfragment.AdhellTurnOnDialogFragment;
import com.layoutxml.sabs.dialogfragment.DnsChangeDialogFragment;
import com.layoutxml.sabs.dialogfragment.NoInternetConnectionDialogFragment;
import com.layoutxml.sabs.fragments.AdhellNotSupportedFragment;
import com.layoutxml.sabs.fragments.AdhellPermissionInfoFragment;
import com.layoutxml.sabs.fragments.AppListFragment;
import com.layoutxml.sabs.fragments.AppSupportFragment;
import com.layoutxml.sabs.fragments.BlockedUrlSettingFragment;
import com.layoutxml.sabs.fragments.BlockerFragment;
import com.layoutxml.sabs.fragments.PackageDisablerFragment;
import com.layoutxml.sabs.service.BlockedDomainService;
import com.layoutxml.sabs.utils.AdhellAppIntegrity;
import com.layoutxml.sabs.utils.DeviceAdminInteractor;

public class MainActivity extends AppCompatActivity {
    public static final String ADHELL_STANDARD_PACKAGE = "http://getadhell.com/standard-package.txt";
    private static final String TAG = MainActivity.class.getCanonicalName();
    private static final String BACK_STACK_TAB_TAG = "tab_fragment";
    protected DeviceAdminInteractor mAdminInteractor;
    private FragmentManager fragmentManager;
    private AdhellNotSupportedDialogFragment adhellNotSupportedDialogFragment;
    private AdhellTurnOnDialogFragment adhellTurnOnDialogFragment;
    private NoInternetConnectionDialogFragment noInternetConnectionDialogFragment;
    BottomNavigationView bottomNavigationView;



    @Override
    public void onBackPressed() {
        int count = fragmentManager.getBackStackEntryCount();
        if (count <= 1) {
            finish();
        } else {
            fragmentManager.popBackStackImmediate();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.MainAppTheme);
        setContentView(R.layout.activity_main);
        setTheme(R.style.MainAppTheme);

        fragmentManager = getSupportFragmentManager();
        mAdminInteractor = DeviceAdminInteractor.getInstance();
        adhellNotSupportedDialogFragment = AdhellNotSupportedDialogFragment.newInstance("App not supported");
        if (!mAdminInteractor.isContentBlockerSupported()) {
            Log.i(TAG, "Device not supported");
            return;
        }

        adhellTurnOnDialogFragment = AdhellTurnOnDialogFragment.newInstance("Adhell Turn On");
        noInternetConnectionDialogFragment = NoInternetConnectionDialogFragment.newInstance("No Internet connection");
        adhellNotSupportedDialogFragment.setCancelable(false);
        adhellTurnOnDialogFragment.setCancelable(false);
        noInternetConnectionDialogFragment.setCancelable(false);

        if (!mAdminInteractor.isContentBlockerSupported()) {
            return;
        }

        bottomNavigationView = (BottomNavigationView)findViewById(R.id.bottom_navigation);
        bottomNavigationView.setSelectedItemId(R.id.blockerTab);
        Fragment defaultFragment = new BlockerFragment();
        fragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, defaultFragment)
                .addToBackStack(BACK_STACK_TAB_TAG)
                .commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(
                new BottomNavigationView.OnNavigationItemSelectedListener() {
                    Fragment replacing;
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.blockerTab:
                                replacing = new BlockerFragment();
                                break;
                            case R.id.packageDisablerTab:
                                replacing = new PackageDisablerFragment();
                                break;
                            case R.id.appPermissionsTab:
                                replacing = new AdhellPermissionInfoFragment();
                                break;
                        }
                        fragmentManager.beginTransaction()
                                .replace(R.id.fragmentContainer, replacing)
                                .addToBackStack(BACK_STACK_TAB_TAG)
                                .commit();
                        return true;
                    }
                });

        AsyncTask.execute(() -> {
//        HeartbeatAlarmHelper.scheduleAlarm();
            AdhellAppIntegrity adhellAppIntegrity = new AdhellAppIntegrity();
//            adhellAppIntegrity.check();
            adhellAppIntegrity.checkDefaultPolicyExists();
            adhellAppIntegrity.checkAdhellStandardPackage();
            adhellAppIntegrity.fillPackageDb();
        });
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
        Log.d(TAG, "onResume1");
        if (!mAdminInteractor.isContentBlockerSupported()) {
            Log.i(TAG, "Device not supported");
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, new AdhellNotSupportedFragment());
            fragmentTransaction.commit();
            return;
        }
        showDialog();
        if (!mAdminInteractor.isActiveAdmin()) {
            Log.d(TAG, "Admin not active");
            return;
        }

        if (!mAdminInteractor.isKnoxEnabled()) {
            Log.d(TAG, "Knox disabled");
            return;
        }
        Log.d(TAG, "Everything is okay");


        ContentBlocker contentBlocker = mAdminInteractor.getContentBlocker();
        if (contentBlocker != null && contentBlocker.isEnabled() && (contentBlocker instanceof ContentBlocker56
                || contentBlocker instanceof ContentBlocker57)) {
            Intent i = new Intent(App.get().getApplicationContext(), BlockedDomainService.class);
            i.putExtra("launchedFrom", "main-activity");
            App.get().getApplicationContext().startService(i);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "Destroying activity");
    }


    public void showDialog() {
        if (!(DeviceAdminInteractor.isSamsung() && mAdminInteractor.isKnoxSupported())) {
            Log.i(TAG, "Device not supported");
            if (!adhellNotSupportedDialogFragment.isVisible()) {
                adhellNotSupportedDialogFragment.show(fragmentManager, "dialog_fragment_adhell_not_supported");
            }
            return;
        }
        if (!mAdminInteractor.isActiveAdmin()) {
            Log.d(TAG, "Admin is not active. Request enabling");
            if (!adhellTurnOnDialogFragment.isVisible()) {
                adhellTurnOnDialogFragment.show(fragmentManager, "dialog_fragment_turn_on_adhell");
            }
            return;
        }

        if (!mAdminInteractor.isKnoxEnabled()) {
            Log.d(TAG, "Knox disabled");
            Log.d(TAG, "Checking if internet connection exists");
            ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
            Log.d(TAG, "Is internet connection exists: " + isConnected);
            if (!isConnected) {
                if (!noInternetConnectionDialogFragment.isVisible()) {
                    noInternetConnectionDialogFragment.show(fragmentManager, "dialog_fragment_no_internet_connection");
                }
            } else {
                if (!adhellTurnOnDialogFragment.isVisible()) {
                    adhellTurnOnDialogFragment.show(fragmentManager, "dialog_fragment_turn_on_adhell");
                }
            }
        }
    }

    public void editLayoutClick(View view) {
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, new BlockedUrlSettingFragment());
            fragmentTransaction.addToBackStack("main_to_editUrl");
            fragmentTransaction.commit();
    }

    public void dnsLayoutClick(View view) {
        DnsChangeDialogFragment dnsChangeDialogFragment = DnsChangeDialogFragment.newInstance("Some title");
                dnsChangeDialogFragment.show(fragmentManager, "dialog_fragment_dns");
    }

    public void alloAppsLayout(View view) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, new AppListFragment());
                fragmentTransaction.addToBackStack("main_to_editApp");
                fragmentTransaction.commit();
    }
}
