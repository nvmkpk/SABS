package com.layoutxml.sabs.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.layoutxml.sabs.BuildConfig;
import com.layoutxml.sabs.R;
import com.layoutxml.sabs.viewmodel.SharedBillingViewModel;

public class OnlyPremiumFragment extends LifecycleFragment {
    private AppCompatActivity parentActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = (AppCompatActivity) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        //Reused onlypremiumfragment for about fragment because I have no idea how to create new fragments. Simple way of creating java class didnt't work so I guess fragments have to be declared somewhere. But where?

        getActivity().setTitle(R.string.only_for_premium_title);
        View view = inflater.inflate(R.layout.fragment_only_premium, container, false);

        TextView versionname = view.findViewById(R.id.version);
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        versionname.setText("Version : " + versionName + " (internal code: " + versionCode + ")");

        EditText knoxKeyEditText = view.findViewById(R.id.knox_key_editText);
        Button knoxKeyButton = view.findViewById(R.id.submit_knox_key_button);
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        String knoxKey = sharedPreferences.getString("knox_key", null);
        if (knoxKey!=null) {
            knoxKeyEditText.setText(knoxKey);
        }
        knoxKeyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("knox_key", knoxKeyEditText.getText().toString());
                editor.commit();
            }
        });

        return view;
    }
}
