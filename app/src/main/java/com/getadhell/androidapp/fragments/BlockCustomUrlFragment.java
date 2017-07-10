package com.getadhell.androidapp.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.getadhell.androidapp.MainActivity;
import com.getadhell.androidapp.R;
import com.getadhell.androidapp.db.AppDatabase;
import com.getadhell.androidapp.db.entity.BlockUrl;
import com.getadhell.androidapp.db.entity.BlockUrlProvider;

import java.util.ArrayList;
import java.util.List;

public class BlockCustomUrlFragment extends LifecycleFragment {

    private List<String> customUrlsToBlock;
    private Context context;
    private AppDatabase appDatabase;
    private ArrayAdapter<String> itemsAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appDatabase = AppDatabase.getAppDatabase(getContext());
        customUrlsToBlock = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        context = this.getActivity();
        View view = inflater.inflate(R.layout.fragment_manual_url_block, container, false);
        ListView listView = (ListView) view.findViewById(R.id.customUrlsListView);
        appDatabase.blockUrlDao()
                .getUrlsLiveDataByProviderId(-1)
                .observe(this, blockUrls -> {
                    customUrlsToBlock.clear();
                    if (blockUrls != null) {
                        for (BlockUrl blockUrl : blockUrls) {
                            customUrlsToBlock.add(blockUrl.url);
                        }
                    }
                    itemsAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, customUrlsToBlock);

                    listView.setAdapter(itemsAdapter);
                    AsyncTask.execute(() -> {
                        BlockUrlProvider blockUrlProvider =
                                appDatabase.blockUrlProviderDao().getByUrl(MainActivity.ADHELL_USER_PACKAGE);
                        blockUrlProvider.count = (blockUrls == null) ? 0 : blockUrls.size();
                        appDatabase.blockUrlProviderDao().updateBlockUrlProviders(blockUrlProvider);
                    });
                });
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            AsyncTask.execute(() -> {
                appDatabase.blockUrlDao().deleteByUrl(customUrlsToBlock.get(position));

            });
            itemsAdapter.notifyDataSetChanged();
            Toast.makeText(context, "Url removed", Toast.LENGTH_SHORT).show();
        });

        final EditText addBlockedUrlEditText = (EditText) view.findViewById(R.id.addBlockedUrlEditText);
        Button addCustomBlockedUrlButton = (Button) view.findViewById(R.id.addCustomBlockedUrlButton);
        addCustomBlockedUrlButton.setOnClickListener(v -> {
            String urlToAdd = addBlockedUrlEditText.getText().toString();
            if (!Patterns.WEB_URL.matcher(urlToAdd).matches()) {
                Toast.makeText(context, "Url not valid. Please check", Toast.LENGTH_SHORT).show();
                return;
            }
            AsyncTask.execute(() -> {
                BlockUrl blockUrl = new BlockUrl(urlToAdd, -1);
                appDatabase.blockUrlDao().insert(blockUrl);
            });
            addBlockedUrlEditText.setText("");
            Toast.makeText(context, "Url has been added", Toast.LENGTH_SHORT).show();
        });
        return view;
    }
}
