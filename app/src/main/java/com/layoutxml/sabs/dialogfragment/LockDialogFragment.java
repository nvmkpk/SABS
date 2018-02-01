package com.layoutxml.sabs.dialogfragment;

/**
 * Created by LayoutXML on 01/02/2018.
 */

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.layoutxml.sabs.R;
import com.layoutxml.sabs.blocker.ContentBlocker57;
import com.layoutxml.sabs.utils.DeviceAdminInteractor;

public class LockDialogFragment extends DialogFragment {
    private Button okLockBtn;
    private Button exitLockBtn;

    public LockDialogFragment() {
    }

    public static LockDialogFragment newInstance(String title) {
        LockDialogFragment lockDialogFragment = new LockDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        lockDialogFragment.setArguments(args);
        return lockDialogFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_lock, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        okLockBtn = view.findViewById(R.id.okLock);
        exitLockBtn = view.findViewById(R.id.exitLock);

        okLockBtn.setOnClickListener(v ->
        {
            dismiss();
        });

        exitLockBtn.setOnClickListener(v ->
        {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }
}
