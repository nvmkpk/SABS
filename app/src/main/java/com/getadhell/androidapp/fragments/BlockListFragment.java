package com.getadhell.androidapp.fragments;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.getadhell.androidapp.R;
import com.getadhell.androidapp.contentprovider.ServerContentBlockProvider;
import com.getadhell.androidapp.model.BlockDb;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

/**
 * Created by Patrick.Lower on 1/17/2017.
 */

public class BlockListFragment extends Fragment {
    private static final String LOG_TAG = BlockListFragment.class.getCanonicalName();
    ListView blockListView;
    String WHITELIST = "whitelist.json";
    Boolean onWhiteList = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.block_list_fragment, container, false);

        blockListView = (ListView) view.findViewById(R.id.urlList);
        new AdhellGetListTask().execute(false);

        final Button editButton = (Button) view.findViewById(R.id.whitelist);
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(LOG_TAG, "Whitelist button click in BlockListFragment");
                if (onWhiteList) {
                    onWhiteList = false;
                    editButton.setText(R.string.whitelist);
                    new AdhellGetListTask().execute(false);
                } else {
                    onWhiteList = true;
                    editButton.setText(R.string.blocklist);
                    new AdhellGetWhiteListTask().execute(false);
                }
            }

        });

        blockListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String)parent.getItemAtPosition(position);
                if (onWhiteList) {
                    //remove from whitelist
                    removeFromWhiteList(item);
                    new AdhellGetWhiteListTask().execute(false);
                } else {
                    //add to whitelist
                    addToWhiteList(item);
                    new AdhellGetListTask().execute(false);
                }
            }
        });

        Button back = (Button)view.findViewById(R.id.back_to_main);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(LOG_TAG, "Back button click in BlockListFragment");
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, new BlockerFragment());
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    private void setData(ArrayList<String> data) {
        ArrayAdapter<String> arrayAdapter = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            arrayAdapter = new ArrayAdapter<String>(this.getContext(), android.R.layout.simple_list_item_1, data);
        } else {
            arrayAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_list_item_1, data);
        }
        blockListView.setAdapter(arrayAdapter);
    }

    private class AdhellGetWhiteListTask extends AsyncTask<Boolean, Void, ArrayList<String>> {

        protected void onPreExecute() {

        }

        protected ArrayList<String> doInBackground(Boolean... switchers) {
            return getWhiteList();
        }

        protected void onPostExecute(ArrayList<String> result) {
            setData(result);
        }
    }

    private class AdhellGetListTask extends AsyncTask<Boolean, Void, ArrayList<String>> {

        protected void onPreExecute() {

        }

        protected ArrayList<String> doInBackground(Boolean... switchers) {
            ServerContentBlockProvider contentProvider = new ServerContentBlockProvider(getContext().getFilesDir());
            BlockDb bdb = contentProvider.loadBlockDb();
            return (ArrayList)bdb.urlsToBlock;
        }

        protected void onPostExecute(ArrayList<String> result) {
            setData(result);
        }
    }

    private void removeFromWhiteList(String url) {
        ArrayList<String> whitelist = getWhiteList();
        whitelist.remove(url);
        File file;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            file = new File(getContext().getFilesDir(), WHITELIST);
        } else {
            file = new File(getActivity().getFilesDir(), WHITELIST);
        }
        if (file.exists()) {
            try {
                Writer output = new BufferedWriter(new FileWriter(file));
                Gson gson = new Gson();
                output.write(gson.toJson(whitelist));
                output.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void addToWhiteList(String url) {
        ArrayList<String> whitelist = getWhiteList();
        whitelist.add(url);
        File file;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            file = new File(getContext().getFilesDir(), WHITELIST);
        } else {
            file = new File(getActivity().getFilesDir(), WHITELIST);
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Writer output = new BufferedWriter(new FileWriter(file));
            Gson gson = new Gson();
            output.write(gson.toJson(whitelist));
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> getWhiteList() {
        File file;
        ArrayList<String> whitelist = new ArrayList<String>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            file = new File(getContext().getFilesDir(), WHITELIST);
        } else {
            file = new File(getActivity().getFilesDir(), WHITELIST);
        }
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                Gson gson = new Gson();
                whitelist = gson.fromJson(reader, ArrayList.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return whitelist;
    }
}
