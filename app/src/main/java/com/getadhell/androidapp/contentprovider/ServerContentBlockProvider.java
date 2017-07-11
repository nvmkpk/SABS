package com.getadhell.androidapp.contentprovider;


import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ServerContentBlockProvider {
    private static final String TAG = ServerContentBlockProvider.class.getCanonicalName();
    private String APPLIST = "applist.json";
    private File filesDir;

    public ServerContentBlockProvider(File filesDir) {
        this.filesDir = filesDir;
    }

    public List<String> loadAllowApps() {
        File file = new File(filesDir, APPLIST);
        List<String> appList = new ArrayList<>();
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                Gson gson = new Gson();
                appList = gson.fromJson(reader, List.class);
                if (appList == null) {
                    appList = new ArrayList<>();
                }
            } catch (FileNotFoundException e) {
                Log.e(TAG, "Problem with loading allowed app: ", e);
                appList = new ArrayList<>();
            }
        }
        return appList;
    }
}
