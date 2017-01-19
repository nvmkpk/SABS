package com.getadhell.androidapp.contentprovider;


import android.util.Log;

import com.getadhell.androidapp.model.BlockDb;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServerContentBlockProvider {
    private Gson gson;
    String WHITELIST = "whitelist.json";
    String APPLIST = "applist.json";
    private File filesDir;

    private static final String TAG = ServerContentBlockProvider.class.getCanonicalName();
    private static final String BLOCK_PROVIDER_URL = "http://getadhell.com/urls-to-block.json";

    public ServerContentBlockProvider(File filesDir) {
        this.gson = new Gson();
        this.filesDir = filesDir;
    }

    public BlockDb loadBlockDb() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(BLOCK_PROVIDER_URL)
                .build();

        Response response = null;
        try {
            response = client.newCall(request).execute();
            String blockDbString = response.body().string();
            BlockDb remoteList = gson.fromJson(blockDbString, BlockDb.class);
            remoteList.urlsToBlock.removeAll(getWhiteList());
            Collections.sort(remoteList.urlsToBlock);
            return remoteList;
        } catch (IOException e) {
            Log.e(TAG, "Failed to load urls from server", e);
        }

        return null;
    }

    public List<String> loadAllowApps() {
        File file;
        List<String> appList = new ArrayList<String>();
        file = new File(filesDir, APPLIST);
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                Gson gson = new Gson();
                appList = gson.fromJson(reader, List.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return appList;
    }

    private ArrayList<String> getWhiteList() {
        File file;
        ArrayList<String> whitelist = new ArrayList<String>();
        file = new File(filesDir, WHITELIST);
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
