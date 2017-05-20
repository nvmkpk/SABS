package com.getadhell.androidapp.fragments;

import android.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.R;
import com.getadhell.androidapp.adapter.BlockedDomainCursorAdapter;
import com.getadhell.androidapp.model.BlockedDomain;
import com.getadhell.androidapp.utils.AdhellDatabaseHelper;

import java.util.List;


public class AdhellReportsFragment extends Fragment {
    private TextView lastDayBlockedTextView;
    private ListView blockedDomainsListView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_adhell_reports, container, false);
        lastDayBlockedTextView = (TextView) view.findViewById(R.id.lastDayBlockedTextView);
        blockedDomainsListView = (ListView) view.findViewById(R.id.blockedDomainsListView);
        AdhellDatabaseHelper adhellDatabaseHelper = AdhellDatabaseHelper.getInstance(
                App.get().getApplicationContext());
        long timestamp = System.currentTimeMillis() / 1000;

        List<BlockedDomain> blockedDomainLast24Hours =
                adhellDatabaseHelper.getBlockedDomainsBetween(timestamp - 24 * 3600, timestamp);
        int size = blockedDomainLast24Hours.size();
        lastDayBlockedTextView.setText(String.valueOf(size));
        Cursor cursor = AdhellDatabaseHelper.getInstance(App.get().getApplicationContext()).getCursorBlockedDomainsBetween(timestamp - 24 * 3600, timestamp);
        BlockedDomainCursorAdapter blockedDomainCursorAdapter = new BlockedDomainCursorAdapter(this.getActivity(), cursor);
        blockedDomainsListView.setAdapter(blockedDomainCursorAdapter);
        return view;
    }

}
