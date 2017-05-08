package com.getadhell.androidapp.fragments;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getadhell.androidapp.R;


public class KnoxActivationFailedFragment extends Fragment {
    private static final String TAG = KnoxActivationFailedFragment.class.getCanonicalName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_knox_license_activation_failed, container, false);
        return view;
    }
}
