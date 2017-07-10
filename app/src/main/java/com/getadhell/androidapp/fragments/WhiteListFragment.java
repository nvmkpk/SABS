package com.getadhell.androidapp.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.getadhell.androidapp.R;

import java.util.ArrayList;
import java.util.List;


public class WhiteListFragment extends Fragment {
    private static final String TAG = WhiteListFragment.class.getCanonicalName();
    private ListView blockListView;
    private ArrayAdapter<String> arrayAdapter;
    private List<AsyncTask> runningTaskList = new ArrayList<>();
    private List<String> whitelist;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_white_list, container, false);


        blockListView = (ListView) view.findViewById(R.id.urlList);
        blockListView.setOnItemClickListener((parent, view1, position, id) -> {
            String item = (String) parent.getItemAtPosition(position);

        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        for (AsyncTask task : runningTaskList) {
            if (task != null && task.getStatus() == AsyncTask.Status.RUNNING)
                task.cancel(true);
        }
    }

}
