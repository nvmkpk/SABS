package com.getadhell.androidapp.fragments;


import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.getadhell.androidapp.R;

public class AppSupportFragment extends Fragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        getActivity().setTitle(getString(R.string.app_support_fragment_title));
        return inflater.inflate(R.layout.fragment_app_support, container, false);
    }
}
