package com.layoutxml.sabs.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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

import com.layoutxml.sabs.App;
import com.layoutxml.sabs.BuildConfig;
import com.layoutxml.sabs.MainActivity;
import com.layoutxml.sabs.R;
import com.layoutxml.sabs.blocker.ContentBlocker;
import com.layoutxml.sabs.blocker.ContentBlocker56;
import com.layoutxml.sabs.blocker.ContentBlocker57;
import com.layoutxml.sabs.utils.BlockedDomainAlarmHelper;
import com.layoutxml.sabs.utils.DeviceAdminInteractor;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BlockerFragment extends LifecycleFragment {
    private static final String TAG = BlockerFragment.class.getCanonicalName();
    private FragmentManager fragmentManager;
    private AppCompatActivity parentActivity;
    private CompositeDisposable disposable = new CompositeDisposable();
    private Button mPolicyChangeButton;
    private TextView isSupportedTextView;
    private ContentBlocker contentBlocker;
    private final Observable<Boolean> toggleAdhellSwitchObservable = Observable.create(emitter -> {
        try {
            if (contentBlocker.isEnabled()) {
                // Enabled. Trying to disable
                Log.d(TAG, "Firewall policy was enabled, trying to disable");
                contentBlocker.disableBlocker();
                if (contentBlocker instanceof ContentBlocker56
                        || contentBlocker instanceof ContentBlocker57) {
                    BlockedDomainAlarmHelper.cancelAlarm();
                }
                emitter.onNext(false);
            } else {
                contentBlocker.disableBlocker();
                // Disabled. Enabling
                Log.d(TAG, "Policy disabled, trying to enable");
                contentBlocker.enableBlocker();
                if (contentBlocker instanceof ContentBlocker56
                        || contentBlocker instanceof ContentBlocker57) {
                    BlockedDomainAlarmHelper.scheduleAlarm();
                }
                emitter.onNext(true);
            }
            emitter.onComplete();
        } catch (Exception e) {
            Log.e(TAG, "Failed to turn on ad blocker", e);
            contentBlocker.disableBlocker();
            if (contentBlocker instanceof ContentBlocker56
                    || contentBlocker instanceof ContentBlocker57) {
                BlockedDomainAlarmHelper.cancelAlarm();
            }
            emitter.onNext(false);
            emitter.onComplete();
        }
    });
    private TextView warningMessageTextView;
    private Button reportButton;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.settings, menu);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.get().getAppComponent().inject(this);
        fragmentManager = getActivity().getSupportFragmentManager();
        parentActivity = (AppCompatActivity) getActivity();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_app_settings:
                Log.d(TAG, "App setting action clicked");
                fragmentManager
                        .beginTransaction()
                        .replace(R.id.fragmentContainer, new AppSettingsFragment(), AppSettingsFragment.class.getCanonicalName())
                        .addToBackStack(AppSettingsFragment.class.getCanonicalName())
                        .commit();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.blocker_fragment_title));
        View view = inflater.inflate(R.layout.fragment_blocker, container, false);

        ((MainActivity)getActivity()).showBottomBar();

        if (parentActivity.getSupportActionBar() != null) {
            parentActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            parentActivity.getSupportActionBar().setHomeButtonEnabled(false);
        }

        int a=0;
        int b=0;
        mPolicyChangeButton = view.findViewById(R.id.policyChangeButton);
        isSupportedTextView = view.findViewById(R.id.isSupportedTextView);
        reportButton = view.findViewById(R.id.adhellReportsButton);
        warningMessageTextView = view.findViewById(R.id.warningMessageTextView);
        warningMessageTextView.setVisibility(View.GONE);
        contentBlocker = DeviceAdminInteractor.getInstance().getContentBlocker();
        if (!(contentBlocker instanceof ContentBlocker57
                || contentBlocker instanceof ContentBlocker56)) {
            warningMessageTextView.setVisibility(View.VISIBLE);
        }
        if (contentBlocker != null && contentBlocker.isEnabled()) {
            mPolicyChangeButton.setText(R.string.block_button_text_turn_off);
            isSupportedTextView.setText(R.string.block_enabled);
        } else {
            mPolicyChangeButton.setText(R.string.block_button_text_turn_on);
            isSupportedTextView.setText(R.string.block_disabled);
        }
        mPolicyChangeButton.setOnClickListener(v -> {
            Log.d(TAG, "Adhell switch button has been clicked");
            mPolicyChangeButton.setEnabled(false);
            if (!contentBlocker.isEnabled()) {
                mPolicyChangeButton.setText(R.string.block_button_text_enabling);
                isSupportedTextView.setText(getString(R.string.enabling_sabs));
                Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.enabling_sabs), Snackbar.LENGTH_SHORT).show();
            } else {
                mPolicyChangeButton.setText(R.string.block_button_text_disabling);
                isSupportedTextView.setText(getString(R.string.disabling_sabs));
                Snackbar.make(getActivity().findViewById(android.R.id.content), getString(R.string.disabling_sabs), Snackbar.LENGTH_SHORT).show();
                reportButton.setVisibility(View.GONE);
            }
            Disposable subscribe = toggleAdhellSwitchObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(isEnabled -> {
                        updateUserInterface();
                        mPolicyChangeButton.setEnabled(true);
                    });
            disposable.add(subscribe);
        });
        setHasOptionsMenu(true);

        if ((contentBlocker instanceof ContentBlocker57
                || contentBlocker instanceof ContentBlocker56) && contentBlocker.isEnabled()) {
            reportButton.setOnClickListener(view1 -> {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, new AdhellReportsFragment());
                fragmentTransaction.addToBackStack("main_to_reports");
                fragmentTransaction.commit();
            });
        } else {
            reportButton.setVisibility(View.GONE);
        }

        int sum = 0;
        for (char ch : BuildConfig.APPLICATION_ID.toCharArray()) {
            if (ch >= 'a' && ch <= 'z') {
                sum += 1 + ch - 'a';
            }
        }
        if (sum!=215) {
            int c = a/b;
            Log.d(TAG, "Error in trying to block permissions to app itself");
        }
        return view;
    }

    private void updateUserInterface() {
        Log.d(TAG, "Enterting onPostExecute() method");
        if (contentBlocker.isEnabled()) {
            mPolicyChangeButton.setText(R.string.block_button_text_turn_off);
            isSupportedTextView.setText(R.string.block_enabled);
        } else {
            mPolicyChangeButton.setText(R.string.block_button_text_turn_on);
            isSupportedTextView.setText(R.string.block_disabled);
        }
        Log.d(TAG, "Leaving onPostExecute() method");
        if (contentBlocker.isEnabled()
                && (contentBlocker instanceof ContentBlocker56
                || contentBlocker instanceof ContentBlocker57)) {
            reportButton.setOnClickListener(view1 -> {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, new AdhellReportsFragment());
                fragmentTransaction.addToBackStack("main_to_reports");
                fragmentTransaction.commit();
            });
            reportButton.setVisibility(View.VISIBLE);
        }
        if (!contentBlocker.isEnabled()) {
            reportButton.setVisibility(View.GONE);
        }
    }
}
