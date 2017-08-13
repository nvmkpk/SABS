package com.getadhell.androidapp.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getadhell.androidapp.R;

public class AdhellNotSupportedFragment extends LifecycleFragment {
    private static final String TAG = AdhellNotSupportedFragment.class.getCanonicalName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adhell_not_supported, container, false);
        return view;
    }

}
