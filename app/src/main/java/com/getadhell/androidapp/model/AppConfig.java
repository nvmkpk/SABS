package com.getadhell.androidapp.model;

import android.content.Context;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AppConfig {
    private static final String LOG_TAG = AppConfig.class.getCanonicalName();
    private final static String ASSET_CONFIG = "config.json";
    public String knoxStandardSdkKey;


    public static AppConfig loadConfigFromAsset(Context context) throws IOException {
        Gson gson = new Gson();
        InputStream inputStream = context.getAssets().open(ASSET_CONFIG);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        return gson.fromJson(bufferedReader, AppConfig.class);
    }
}
