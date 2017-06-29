package com.getadhell.androidapp.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.getadhell.androidapp.R;
import com.getadhell.androidapp.db.entity.BlockUrlProvider;

import java.util.List;

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
        if (blockUrlProvider != null) {
            Log.d(TAG, blockUrlProvider.url);
            blockUrlProviderTextView.setText(blockUrlProvider.url + "");
            blockUrlCountTextView.setText(blockUrlProvider.count + "");

        }

        return convertView;
    }
}
