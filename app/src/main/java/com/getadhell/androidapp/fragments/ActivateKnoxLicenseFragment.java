package com.getadhell.androidapp.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
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

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ActivateKnoxLicenseFragment extends Fragment {
    private static final String TAG = ActivateKnoxLicenseFragment.class.getCanonicalName();
    private static FragmentManager fragmentManager;
    private Button mActivateKnoxButton;
    private CompositeDisposable disposable = new CompositeDisposable();
    private DeviceAdminInteractor deviceAdminInteractor;

    private final Single<String> knoxKeyObservable = Single.create(emmiter -> {
        String knoxKey = deviceAdminInteractor.getKnoxKey();
        emmiter.onSuccess(knoxKey);
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_knox_license_activation, container, false);
        deviceAdminInteractor = DeviceAdminInteractor.getInstance();
        fragmentManager = this.getFragmentManager();
        mActivateKnoxButton = (Button) view.findViewById(R.id.activateKnoxButton);
        mActivateKnoxButton.setOnClickListener(v -> {
            mActivateKnoxButton.setEnabled(false);
            mActivateKnoxButton.setText(R.string.activating_knox_license);
            Disposable subscribe = knoxKeyObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(knoxKey -> deviceAdminInteractor.forceActivateKnox(knoxKey));
            disposable.add(subscribe);
            Log.d(TAG, "Button click in Fragment1");
        });
        return view;
    }

    public static class MyReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            DeviceAdminInteractor deviceAdminInteractor = DeviceAdminInteractor.getInstance();
            if (deviceAdminInteractor.isKnoxEnbaled()) {
                Toast.makeText(context, "License activated", Toast.LENGTH_LONG).show();
                Log.d(TAG, "License activated");
            } else {
                Toast.makeText(context, "License activation failed. Try again", Toast.LENGTH_LONG).show();
                Log.w(TAG, "License activation failed");
            }
            if (DeviceUtils.isContentBlockerSupported()
                    && deviceAdminInteractor.isKnoxEnbaled()) {
                if (fragmentManager != null) {
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragmentContainer, new BlockerFragment());
                    fragmentTransaction.commitAllowingStateLoss();
                    return;
                }
            }
            if (fragmentManager != null) {
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, new KnoxActivationFailedFragment());
                fragmentTransaction.commitAllowingStateLoss();
            }
        }
    }
}
