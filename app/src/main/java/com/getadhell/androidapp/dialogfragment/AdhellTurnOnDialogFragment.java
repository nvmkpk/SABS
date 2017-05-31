package com.getadhell.androidapp.dialogfragment;


import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.getadhell.androidapp.R;
import com.getadhell.androidapp.deviceadmin.DeviceAdminInteractor;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class AdhellTurnOnDialogFragment extends DialogFragment {
    private static final String TAG = AdhellTurnOnDialogFragment.class.getCanonicalName();
    private DeviceAdminInteractor deviceAdminInteractor;
    private final Single<String> knoxKeyObservable = Single.create(emmiter -> {
        String knoxKey = deviceAdminInteractor.getKnoxKey();
        emmiter.onSuccess(knoxKey);
    });
    private Button turnOnAdminButton;
    private Button activateKnoxButton;
    private Button dismissDialogButton;
    private CompositeDisposable disposable = new CompositeDisposable();

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            DeviceAdminInteractor deviceAdminInteractor = DeviceAdminInteractor.getInstance();
            if (deviceAdminInteractor.isKnoxEnbaled()) {
                Toast.makeText(context, "License activated", Toast.LENGTH_LONG).show();
                allowDialogDismiss(true);
                dismiss();
                allowActivateKnox(false);
                activateKnoxButton.setText("License Activated");
                Log.d(TAG, "License activated");
            } else {
                Toast.makeText(context, "License activation failed. Try again", Toast.LENGTH_LONG).show();
                Log.w(TAG, "License activation failed");
            }
        }
    };

    public AdhellTurnOnDialogFragment() {
        deviceAdminInteractor = DeviceAdminInteractor.getInstance();
    }

    public static AdhellTurnOnDialogFragment newInstance(String title) {
        AdhellTurnOnDialogFragment adhellTurnOnDialogFragment = new AdhellTurnOnDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        adhellTurnOnDialogFragment.setArguments(args);
        return adhellTurnOnDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_turn_on_adhell, container);
        turnOnAdminButton = (Button) view.findViewById(R.id.turnOnAdminButton);
        activateKnoxButton = (Button) view.findViewById(R.id.activateKnoxButton);
        dismissDialogButton = (Button) view.findViewById(R.id.dismissDialogButton);

        turnOnAdminButton.setOnClickListener(v -> {
            deviceAdminInteractor.forceEnableAdmin(this.getActivity());
        });


        // TODO: Implement on error
        activateKnoxButton.setOnClickListener(v -> {
            Log.d(TAG, "Activate Knox button clicked");
            activateKnoxButton.setEnabled(false);
            activateKnoxButton.setText(R.string.activating_knox_license);
            Disposable subscribe = knoxKeyObservable
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeOn(Schedulers.io())
                    .subscribe(knoxKey -> deviceAdminInteractor.forceActivateKnox(knoxKey));
            disposable.add(subscribe);
        });

        dismissDialogButton.setOnClickListener(v -> {
            dismiss();
        });
        allowDialogDismiss(false);
        IntentFilter filter = new IntentFilter();
        filter.addAction("edm.intent.action.license.status");
        this.getActivity().registerReceiver(receiver, filter);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "AdhellTurnOnDialogFragment on Resume");
        if (deviceAdminInteractor.isActiveAdmin()) {
            allowTurnOnAdmin(false);
            turnOnAdminButton.setText("Admin Enabled");
        } else {
            allowTurnOnAdmin(true);
            turnOnAdminButton.setText("Enable Admin");
        }
        if (deviceAdminInteractor.isKnoxEnbaled()) {
            activateKnoxButton.setText("License activated");
            allowActivateKnox(false);
        } else {
            if (!deviceAdminInteractor.isActiveAdmin()) {
                activateKnoxButton.setText("Activate License");
                allowActivateKnox(false);
            } else {
                activateKnoxButton.setText("Activate License");
                allowActivateKnox(true);
            }
        }
        if (deviceAdminInteractor.isActiveAdmin() && deviceAdminInteractor.isKnoxEnbaled()) {
            allowDialogDismiss(true);
            dismiss();
        } else {
            allowDialogDismiss(false);
        }
    }

    private void allowDialogDismiss(boolean isAllowed) {
        Log.i(TAG, "allowDialogDismiss");
        if (isAllowed) {
            if (dismissDialogButton != null) {
                dismissDialogButton.setText("Your are ready to go");
                dismissDialogButton.setEnabled(true);
                dismissDialogButton.setClickable(true);
            }
        } else {
            dismissDialogButton.setText("Complete above steps2");
            dismissDialogButton.setEnabled(false);
            dismissDialogButton.setClickable(false);
        }
    }

    private void allowActivateKnox(boolean isAllowed) {
        Log.i(TAG, "allowActivateKnox");
        activateKnoxButton.setEnabled(isAllowed);
        activateKnoxButton.setClickable(isAllowed);
    }

    private void allowTurnOnAdmin(boolean isAllowed) {
        turnOnAdminButton.setClickable(isAllowed);
        turnOnAdminButton.setEnabled(isAllowed);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
