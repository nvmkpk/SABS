package com.getadhell.androidapp.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.R;
import com.getadhell.androidapp.adapter.BlockUrlProviderAdapter;
import com.getadhell.androidapp.db.AppDatabase;
import com.getadhell.androidapp.db.entity.BlockUrl;
import com.getadhell.androidapp.db.entity.BlockUrlProvider;
import com.getadhell.androidapp.viewmodel.BlockUrlProvidersViewModel;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CustomBlockUrlProviderFragment extends LifecycleFragment {

    private static final String TAG = CustomBlockUrlProviderFragment.class.getCanonicalName();
    private AppDatabase mDb;
    private EditText blockUrlProviderEditText;
    private Button addBlockUrlProviderButton;
    private ListView blockListView;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDb = AppDatabase.getAppDatabase(App.get().getApplicationContext());


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_custom_url_provider, container, false);
        blockUrlProviderEditText = (EditText) view.findViewById(R.id.blockUrlProviderEditText);
        addBlockUrlProviderButton = (Button) view.findViewById(R.id.addBlockUrlProviderButton);
        blockListView = (ListView) view.findViewById(R.id.blockUrlProviderListView);

        addBlockUrlProviderButton.setOnClickListener(v -> {
            String urlProvider = blockUrlProviderEditText.getText().toString();
            // Check if normal url
            if (!urlProvider.isEmpty() && URLUtil.isValidUrl(urlProvider)) {
                Maybe.fromCallable(() -> {
                    BlockUrlProvider blockUrlProvider = new BlockUrlProvider();
                    blockUrlProvider.url = urlProvider;
                    blockUrlProvider.count = 0;
                    blockUrlProvider.deletable = true;
                    blockUrlProvider.lastUpdated = new Date();
                    blockUrlProvider.selected = false;
                    blockUrlProvider.id = mDb.blockUrlProviderDao().insertAll(blockUrlProvider)[0];
                    // Try to download and parse urls
                    URL urlProviderUrl = new URL(urlProvider);
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
                    blockUrlProvider.count = blockUrls.size();
                    Log.d(TAG, "Number of urls to insert: " + blockUrlProvider.count);
                    // Save url provider
                    mDb.blockUrlProviderDao().updateBlockUrlProviders(blockUrlProvider);
                    // Save urls from providers
                    mDb.blockUrlDao().insertAll(blockUrls);
                    return null;
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
            }
        });

        BlockUrlProvidersViewModel model = ViewModelProviders.of(getActivity()).get(BlockUrlProvidersViewModel.class);
        model.getBlockUrlProviders().observe(this, blockUrlProviders -> {
            BlockUrlProviderAdapter adapter = new BlockUrlProviderAdapter(this.getContext(), blockUrlProviders);
            blockListView.setAdapter(adapter);
        });

        return view;
    }
}
