package com.getadhell.androidapp.utils;

import android.content.Context;
import android.util.Log;

import com.getadhell.androidapp.App;
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
import java.util.List;

public class AppWhiteList {
    private static final String TAG = AppWhiteList.class.getCanonicalName();
    private static final String APPLIST = "applist.json";
    private Context mContext;

    public AppWhiteList() {
        mContext = App.get().getApplicationContext();
    }

    public void addToWhiteList(String packageName) {
        List<String> whitelist = getWhiteList();
        if (whitelist.contains(packageName)) {
            return;
        }
        whitelist.add(packageName);
        File file;
        file = new File(mContext.getFilesDir(), APPLIST);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "Failed to create appwhitelist file", e);
            }
        }
        try {
            Writer output = new BufferedWriter(new FileWriter(file));
            Gson gson = new Gson();
            output.write(gson.toJson(whitelist));
            output.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed to write json to appwhitelist file", e);
        }
    }

    public void removeFromWhiteList(String packageName) {
        List<String> whitelist = getWhiteList();
        whitelist.remove(packageName);
        File file = new File(mContext.getFilesDir(), APPLIST);
        if (file.exists()) {
            try {
                Writer output = new BufferedWriter(new FileWriter(file));
                Gson gson = new Gson();
                output.write(gson.toJson(whitelist));
                output.close();
            } catch (IOException e) {
                Log.e(TAG, "Failed to delete url from appwhite list file", e);
            }
        }
    }

    public List<String> getWhiteList() {
        File file = new File(mContext.getFilesDir(), APPLIST);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "Problem with creating file", e);
            }
        }
        List<String> whitelist = null;
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Gson gson = new Gson();
            whitelist = gson.fromJson(reader, ArrayList.class);
            if (whitelist == null) {
                Log.w(TAG, "Whitelist is null");
                whitelist = new ArrayList<String>();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Problem with reading whitelist", e);
            whitelist = new ArrayList<String>();
        }
        return whitelist;
    }
}
