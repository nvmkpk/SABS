package com.getadhell.androidapp.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.getadhell.androidapp.R;
import com.getadhell.androidapp.adapter.ReportBlockedUrlAdapter;
import com.getadhell.androidapp.viewmodel.AdhellReportViewModel;


public class AdhellReportsFragment extends LifecycleFragment {
    private TextView lastDayBlockedTextView;
    private ListView blockedDomainsListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adhell_reports, container, false);

        lastDayBlockedTextView = (TextView) view.findViewById(R.id.lastDayBlockedTextView);
        blockedDomainsListView = (ListView) view.findViewById(R.id.blockedDomainsListView);

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
