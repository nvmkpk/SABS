package com.fiendfyre.AdHell2.utils;

import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.URLUtil;

import com.fiendfyre.AdHell2.db.entity.BlockUrl;
import com.fiendfyre.AdHell2.db.entity.BlockUrlProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class BlockUrlUtils {
    private static final String TAG = BlockUrlUtils.class.getCanonicalName();

    @NonNull
    public static List<BlockUrl> loadBlockUrls(BlockUrlProvider blockUrlProvider) throws IOException {
        URL urlProviderUrl = new URL(blockUrlProvider.url);
        URLConnection connection = urlProviderUrl.openConnection();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        List<BlockUrl> blockUrls = new ArrayList<>();
        String inputLine;
        while ((inputLine = bufferedReader.readLine()) != null) {
            Log.d(TAG, "Url: " + inputLine);
            inputLine = inputLine
                    .replace("127.0.0.1", "")
                    .replace("0.0.0.0", "")
                    .trim()
                    .toLowerCase();
            int hIndex = inputLine.indexOf("#");
            if (hIndex != -1) {
                inputLine = inputLine.substring(0, hIndex).trim();
            }

            if (URLUtil.isValidUrl("http://" + inputLine)) {
                BlockUrl blockUrl = new BlockUrl(inputLine, blockUrlProvider.id);
                blockUrls.add(blockUrl);
            }
        }
        bufferedReader.close();
        return blockUrls;
    }
}
