package com.getadhell.androidapp.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;

import com.getadhell.androidapp.viewmodel.BlockUrlProvidersViewModel;

public class CustomBlockUrlProviderFragment extends LifecycleFragment {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BlockUrlProvidersViewModel model = ViewModelProviders.of(getActivity()).get(BlockUrlProvidersViewModel.class);
        model.getBlockUrlProviders().observe(this, blockurlProviders -> {

        });
    }
}
