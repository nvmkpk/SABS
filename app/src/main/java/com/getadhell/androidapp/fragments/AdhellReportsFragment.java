package com.getadhell.androidapp.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.R;
import com.getadhell.androidapp.adapter.ReportBlockedUrlAdapter;
import com.getadhell.androidapp.db.AppDatabase;
import com.getadhell.androidapp.db.entity.ReportBlockedUrl;

import java.util.Date;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class AdhellReportsFragment extends Fragment {
    private TextView lastDayBlockedTextView;
    private ListView blockedDomainsListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adhell_reports, container, false);
        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setHomeButtonEnabled(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        lastDayBlockedTextView = (TextView) view.findViewById(R.id.lastDayBlockedTextView);
        blockedDomainsListView = (ListView) view.findViewById(R.id.blockedDomainsListView);

        AppDatabase appDatabase = AppDatabase.getAppDatabase(App.get().getApplicationContext());

        Maybe.fromCallable(() -> {
            List<ReportBlockedUrl> reportBlockedUrls =
                    appDatabase.reportBlockedUrlDao().getReportBlockUrlBetween(new Date(System.currentTimeMillis() - 24 * 3600 * 1000), new Date());
            ReportBlockedUrlAdapter reportBlockedUrlAdapter = new ReportBlockedUrlAdapter(this.getActivity(), reportBlockedUrls);
            blockedDomainsListView.setAdapter(reportBlockedUrlAdapter);
            lastDayBlockedTextView.setText(String.valueOf(reportBlockedUrls.size()));
            return null;
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
        return view;
    }

}
