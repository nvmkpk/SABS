package com.getadhell.androidapp.fragments;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
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

import com.getadhell.androidapp.R;
import com.getadhell.androidapp.utils.DeviceUtils;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.List;

public class BlockCustomUrlFragment extends Fragment {

    private static final String PREF_CUSTOM_URL = "custom_urls_to_block";
    private static final String CUSTOM_KEY = "urls_key";
    private List<String> customUrlsToBlock;
    private Context context;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        context = this.getActivity();
        View view = inflater.inflate(R.layout.fragment_manual_url_block, container, false);
        customUrlsToBlock = DeviceUtils.loadCustomBlockedUrls();
        final ArrayAdapter<String> itemsAdapter = new ArrayAdapter<>(this.getActivity(), android.R.layout.simple_list_item_1, customUrlsToBlock);

        ListView listView = (ListView) view.findViewById(R.id.customUrlsListView);
        listView.setAdapter(itemsAdapter);
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            customUrlsToBlock.remove(position);
            save();
            itemsAdapter.notifyDataSetChanged();
            Toast.makeText(context, "Url removed", Toast.LENGTH_SHORT).show();
        });

        final EditText addBlockedUrlEditText = (EditText) view.findViewById(R.id.addBlockedUrlEditText);
        Button addCustomBlockedUrlButton = (Button) view.findViewById(R.id.addCustomBlockedUrlButton);
        addCustomBlockedUrlButton.setOnClickListener(view12 ->
        {
            String urlToAdd = addBlockedUrlEditText.getText().toString();
            if (!Patterns.WEB_URL.matcher(urlToAdd).matches()) {
                Toast.makeText(context, "Url not valid. Please check", Toast.LENGTH_SHORT).show();
                return;
            }
            customUrlsToBlock.add(urlToAdd);
            Collections.sort(customUrlsToBlock);
            save();
            addBlockedUrlEditText.setText("");
            Toast.makeText(context, "Url has been added", Toast.LENGTH_SHORT).show();
            itemsAdapter.notifyDataSetChanged();
        });
        return view;
    }


    private void save() {
        SharedPreferences prefs = this.getActivity().getSharedPreferences(PREF_CUSTOM_URL, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String jsonText = gson.toJson(customUrlsToBlock);
        editor.putString(CUSTOM_KEY, jsonText);
        editor.apply();
    }
}
