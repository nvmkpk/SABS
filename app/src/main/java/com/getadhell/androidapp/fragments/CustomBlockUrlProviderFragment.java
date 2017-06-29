package com.getadhell.androidapp.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.getadhell.androidapp.R;
import com.getadhell.androidapp.viewmodel.BlockUrlProvidersViewModel;

public class CustomBlockUrlProviderFragment extends LifecycleFragment {

    private EditText blockUrlProviderEditText;
    private Button addBlockUrlProviderButton;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BlockUrlProvidersViewModel model = ViewModelProviders.of(getActivity()).get(BlockUrlProvidersViewModel.class);
        model.getBlockUrlProviders().observe(this, blockurlProviders -> {

        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_custom_url_provider, container, false);
        blockUrlProviderEditText = (EditText) view.findViewById(R.id.blockUrlProviderEditText);
        
        return view;
    }
}
