package com.getadhell.androidapp.fragments;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.R;
import com.getadhell.androidapp.adapter.BlockUrlProviderAdapter;
import com.getadhell.androidapp.db.AppDatabase;
import com.getadhell.androidapp.db.entity.BlockUrlProvider;
import com.getadhell.androidapp.viewmodel.BlockUrlProvidersViewModel;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class CustomBlockUrlProviderFragment extends LifecycleFragment {

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
            BlockUrlProvider blockUrlProvider = new BlockUrlProvider();
            blockUrlProvider.url = urlProvider;
            blockUrlProvider.count = 10;
            blockUrlProvider.deletable = true;
            blockUrlProvider.lastUpdated = "yesterday";
            blockUrlProvider.selected = false;
            if (!urlProvider.isEmpty()) {
                Maybe.fromCallable(() -> {
                    mDb.blockUrlProviderDao().insertAll(blockUrlProvider);
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
