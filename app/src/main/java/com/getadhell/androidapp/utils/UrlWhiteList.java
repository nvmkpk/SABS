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

public class UrlWhiteList {
    private static final String WHITELIST = "whitelist.json";
    private static final String TAG = UrlWhiteList.class.getCanonicalName();
    private Context mContext;

    public UrlWhiteList() {
        mContext = App.get().getApplicationContext();
    }

    public void removeFromWhiteList(String url) {
        ArrayList<String> whitelist = getWhiteList();
        whitelist.remove(url);
        File file;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            file = new File(mContext.getFilesDir(), WHITELIST);
        } else {
            file = new File(mContext.getFilesDir(), WHITELIST);
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

    public void addToWhiteList(String url) {
        ArrayList<String> whitelist = getWhiteList();
        Log.d(TAG, "Whitelisted apps: " + whitelist.toString());
        Log.d(TAG, "addToWhiteList: " + url);
        if (whitelist.contains(url)) {
            Log.d(TAG, "Already whitelisted: " + url);
            return;
        }
        Log.d(TAG, "Adding to whitelist: " + url);
        whitelist.add(url);
        File file = new File(mContext.getFilesDir(), WHITELIST);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "Failed file creating", e);
            }
        }
        try {
            Writer output = new BufferedWriter(new FileWriter(file));
            Gson gson = new Gson();
            output.write(gson.toJson(whitelist));
            output.close();
        } catch (IOException e) {
            Log.e(TAG, "Failed file writing", e);
        }
    }

    public ArrayList<String> getWhiteList() {
        File file;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            file = new File(mContext.getFilesDir(), WHITELIST);
        } else {
            file = new File(mContext.getFilesDir(), WHITELIST);
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.e(TAG, "Failed file creating", e);
            }
        }
        ArrayList<String> whitelist = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            Gson gson = new Gson();
            whitelist = gson.fromJson(reader, ArrayList.class);
            if (whitelist == null) {
                whitelist = new ArrayList<>();
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Failed to get whitelist: ", e);
            whitelist = new ArrayList<>();
        }
        return whitelist;
    }
}
