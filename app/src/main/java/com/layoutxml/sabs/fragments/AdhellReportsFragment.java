package com.layoutxml.sabs.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.layoutxml.sabs.R;
import com.layoutxml.sabs.adapter.ReportBlockedUrlAdapter;
import com.layoutxml.sabs.viewmodel.AdhellReportViewModel;


public class AdhellReportsFragment extends LifecycleFragment {
    private AppCompatActivity parentActivity;
    private TextView lastDayBlockedTextView;
    private ListView blockedDomainsListView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        parentActivity = (AppCompatActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        parentActivity.setTitle("Recent activity");
        if (parentActivity.getSupportActionBar() != null) {
            parentActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            parentActivity.getSupportActionBar().setHomeButtonEnabled(true);
        }
        View view = inflater.inflate(R.layout.fragment_adhell_reports, container, false);

        lastDayBlockedTextView = view.findViewById(R.id.lastDayBlockedTextView);
        blockedDomainsListView = view.findViewById(R.id.blockedDomainsListView);

        AdhellReportViewModel adhellReportViewModel = ViewModelProviders.of(getActivity()).get(AdhellReportViewModel.class);
        adhellReportViewModel.getReportBlockedUrls().observe(this, reportBlockedUrls -> {
            ReportBlockedUrlAdapter reportBlockedUrlAdapter = new ReportBlockedUrlAdapter(this.getContext(), reportBlockedUrls);
            blockedDomainsListView.setAdapter(reportBlockedUrlAdapter);
            lastDayBlockedTextView.setText(String.valueOf(reportBlockedUrls.size()));
            reportBlockedUrlAdapter.notifyDataSetChanged();
        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}
