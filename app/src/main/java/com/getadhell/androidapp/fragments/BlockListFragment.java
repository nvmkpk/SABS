package com.getadhell.androidapp.fragments;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.getadhell.androidapp.R;
import com.getadhell.androidapp.contentprovider.ServerContentBlockProvider;
import com.getadhell.androidapp.model.BlockDb;
import com.getadhell.androidapp.utils.CustomArrayAdapter;
import com.getadhell.androidapp.utils.UrlWhiteList;

import java.util.ArrayList;
import java.util.List;


public class BlockListFragment extends Fragment {
    private static final String TAG = BlockListFragment.class.getCanonicalName();
    private ListView blockListView;

    private Boolean onWhiteList = false;
    private ArrayAdapter<String> arrayAdapter;
    private List<AsyncTask> runningTaskList = new ArrayList<>();
    private Fragment fragment;
    private UrlWhiteList urlWhiteList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        urlWhiteList = new UrlWhiteList();
        fragment = this;
        final View view = inflater.inflate(R.layout.fragment_block_list, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        blockListView = (ListView) view.findViewById(R.id.urlList);
        new AdhellGetListTask().execute(false);

        blockListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                if (onWhiteList) {
                    //remove from whitelist
                    urlWhiteList.removeFromWhiteList(item);
                    new AdhellGetWhiteListTask().execute(false);
                } else {
                    //add to whitelist
                    urlWhiteList.addToWhiteList(item);
                    new AdhellGetListTask().execute(false);
                }
            }
        });

        TextView filter = (TextView) view.findViewById(R.id.urlFilterEditText);
        filter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (arrayAdapter != null) {
                    arrayAdapter.getFilter().filter(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        TabHost th = (TabHost) view.findViewById(R.id.urlTabHost);
        th.setup();
        th.addTab(th.newTabSpec("BlockTab").setIndicator(getResources().getString(R.string.block_url_tab_label)).setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                return new View(getActivity());
            }
        }));
        th.addTab(th.newTabSpec("AllowTab").setIndicator(getResources().getString(R.string.allowed_url_tab_label)).setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                return new View(getActivity());
            }
        }));

        th.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals("BlockTab")) {
                    onWhiteList = false;
                    new AdhellGetListTask().execute(false);
                }
                if (tabId.equals("AllowTab")) {
                    onWhiteList = true;
                    new AdhellGetWhiteListTask().execute(false);
                }
            }
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

    private void setData(ArrayList<String> data) {
        arrayAdapter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            arrayAdapter = new CustomArrayAdapter(this.getContext(), android.R.layout.simple_list_item_1, data);
        } else {
            arrayAdapter = new CustomArrayAdapter(this.getActivity(), android.R.layout.simple_list_item_1, data);
        }
        blockListView.setAdapter(arrayAdapter);
    }


    private class AdhellGetWhiteListTask extends AsyncTask<Boolean, Void, ArrayList<String>> {

        protected void onPreExecute() {
            runningTaskList.add(this);
        }

        protected ArrayList<String> doInBackground(Boolean... switchers) {
            return urlWhiteList.getWhiteList();
        }

        protected void onPostExecute(ArrayList<String> result) {
            if (!isCancelled())
                setData(result);
            runningTaskList.remove(this);
        }
    }

    private class AdhellGetListTask extends AsyncTask<Boolean, Void, ArrayList<String>> {
        ProgressDialog pd;

        protected void onPreExecute() {
            runningTaskList.add(this);
            pd = ProgressDialog.show(getActivity(), "", "Please Wait, Loading URL List", false);
        }

        protected ArrayList<String> doInBackground(Boolean... switchers) {
            ServerContentBlockProvider contentProvider = new ServerContentBlockProvider(fragment.getActivity().getApplicationContext().getFilesDir());
            BlockDb bdb = contentProvider.loadBlockDb();
            return (ArrayList) bdb.urlsToBlock;
        }

        protected void onPostExecute(ArrayList<String> result) {
            pd.dismiss();
            if (!isCancelled())
                setData(result);
            runningTaskList.remove(this);
        }
    }
}
