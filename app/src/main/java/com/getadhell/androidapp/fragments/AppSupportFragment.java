package com.getadhell.androidapp.fragments;


import android.support.v4.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.getadhell.androidapp.R;

public class AppSupportFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.app_support_fragment_title));
        View view = inflater.inflate(R.layout.fragment_app_support, container, false);
        ImageButton supportDevelopmentButton = (ImageButton) view.findViewById(R.id.supportDevelopmentButton);
        supportDevelopmentButton.setOnClickListener(v -> {
            String url = "http://www.paypal.me/raiym/5USD";
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);
        });
        return view;
    }
}
