package com.getadhell.androidapp.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
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

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BlockerFragment extends Fragment {
    private static final String TAG = BlockerFragment.class.getCanonicalName();
    private CompositeDisposable disposable = new CompositeDisposable();
    private Button mPolicyChangeButton;
    private TextView isSupportedTextView;
    private ContentBlocker contentBlocker;
    private Button reportButton;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.settings, menu);
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
        View view = inflater.inflate(R.layout.fragment_blocker, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        mPolicyChangeButton = (Button) view.findViewById(R.id.policyChangeButton);
        isSupportedTextView = (TextView) view.findViewById(R.id.isSupportedTextView);
        reportButton = (Button) view.findViewById(R.id.adhellReportsButton);

        contentBlocker = DeviceUtils.getContentBlocker();

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
                isSupportedTextView.setText(getString(R.string.please_wait));
            } else {
                mPolicyChangeButton.setText(R.string.block_button_text_disabling);
                isSupportedTextView.setText(getString(R.string.wait_deleting));
                reportButton.setVisibility(View.GONE);
            }
            Disposable subscribe = toggleAdhellSwitchObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(isEnabled -> {
                        updateUserInterface(isEnabled);
                        mPolicyChangeButton.setEnabled(true);
                    });
            disposable.add(subscribe);
        });
        setHasOptionsMenu(true);

        if ((contentBlocker instanceof ContentBlocker57
                || contentBlocker instanceof ContentBlocker56) && contentBlocker.isEnabled()) {
            reportButton.setOnClickListener(view1 -> {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, new AdhellReportsFragment());
                fragmentTransaction.addToBackStack("main_to_reports");
                fragmentTransaction.commit();
            });
        } else {
            reportButton.setVisibility(View.GONE);
        }
        return view;
    }

    private void updateUserInterface(Boolean isEnabled) {
        Log.d(TAG, "Enterting onPostExecute() method");
        if (isEnabled) {
            mPolicyChangeButton.setText(R.string.block_button_text_turn_off);
            isSupportedTextView.setText(R.string.block_enabled);
        } else {
            mPolicyChangeButton.setText(R.string.block_button_text_turn_on);
            isSupportedTextView.setText(R.string.block_disabled);
        }
        Log.d(TAG, "Leaving onPostExecute() method");
        if (isEnabled
                && (contentBlocker instanceof ContentBlocker56
                || contentBlocker instanceof ContentBlocker57)) {
            reportButton.setOnClickListener(view1 -> {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, new AdhellReportsFragment());
                fragmentTransaction.addToBackStack("main_to_reports");
                fragmentTransaction.commit();
            });
            reportButton.setVisibility(View.VISIBLE);
        }
        if (!isEnabled) {
            reportButton.setVisibility(View.GONE);
        }
    }

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
            emitter.onError(e);
        }
    });
}
