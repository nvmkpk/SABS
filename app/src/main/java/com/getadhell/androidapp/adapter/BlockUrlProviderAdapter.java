package com.getadhell.androidapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.R;
import com.getadhell.androidapp.db.AppDatabase;
import com.getadhell.androidapp.db.entity.BlockUrlProvider;

import java.text.SimpleDateFormat;
import java.util.List;

import io.reactivex.Maybe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class BlockUrlProviderAdapter extends ArrayAdapter<BlockUrlProvider> {

    private static final String TAG = BlockUrlProviderAdapter.class.getCanonicalName();

    public BlockUrlProviderAdapter(Context context, List<BlockUrlProvider> blockUrlProviders) {
        super(context, 0, blockUrlProviders);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        BlockUrlProvider blockUrlProvider = getItem(position);
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_block_url_provider, parent, false);
        }
        TextView blockUrlProviderTextView = (TextView) convertView.findViewById(R.id.blockUrlProviderTextView);
        TextView blockUrlCountTextView = (TextView) convertView.findViewById(R.id.blockUrlCountTextView);
        CheckBox urlProviderCheckBox = (CheckBox) convertView.findViewById(R.id.urlProviderCheckBox);
        ImageView deleteUrlImageView = (ImageView) convertView.findViewById(R.id.deleteUrlProviderImageView);
        TextView lastUpdatedTextView = (TextView) convertView.findViewById(R.id.lastUpdatedTextView);
        urlProviderCheckBox.setTag(position);
        deleteUrlImageView.setTag(position);
        if (blockUrlProvider != null) {
            Log.d(TAG, blockUrlProvider.url);
            blockUrlProviderTextView.setText(blockUrlProvider.url + "");
            blockUrlCountTextView.setText(blockUrlProvider.count + "");
            urlProviderCheckBox.setChecked(blockUrlProvider.selected);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            lastUpdatedTextView.setText(dateFormat.format(blockUrlProvider.lastUpdated));
        }
        urlProviderCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            int position2 = (Integer) buttonView.getTag();
            BlockUrlProvider blockUrlProvider2 = getItem(position2);
            if (blockUrlProvider2 != null) {
                blockUrlProvider2.selected = isChecked;
                Maybe.fromCallable(() -> {
                    AppDatabase mDb = AppDatabase.getAppDatabase(App.get().getApplicationContext());
                    mDb.blockUrlProviderDao().updateBlockUrlProviders(blockUrlProvider2);
                    return null;
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
            }
        });

        deleteUrlImageView.setOnClickListener(imageView -> {
            int position2 = (Integer) imageView.getTag();
            BlockUrlProvider blockUrlProvider2 = getItem(position2);
            if (blockUrlProvider2 != null) {
                Maybe.fromCallable(() -> {
                    AppDatabase mDb = AppDatabase.getAppDatabase(App.get().getApplicationContext());
                    mDb.blockUrlProviderDao().delete(blockUrlProvider2);
                    return null;
                })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe();
            }

        });
        return convertView;
    }
}
