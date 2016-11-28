package com.getadhell.androidapp.contentprovider;


import android.util.Log;

import com.getadhell.androidapp.model.BlockDb;
import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ServerContentBlockProvider {
    private Gson gson;

    private static final String TAG = ServerContentBlockProvider.class.getCanonicalName();
    private static final String BLOCK_PROVIDER_URL = "http://getadhell.com/urls-to-block.json";

    public ServerContentBlockProvider() {
        this.gson = new Gson();
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
            return gson.fromJson(blockDbString, BlockDb.class);
        } catch (IOException e) {
            Log.e(TAG, "Failed to load urls from server", e);
        }

        return null;
    }



}
