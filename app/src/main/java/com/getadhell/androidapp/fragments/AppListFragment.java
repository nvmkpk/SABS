package com.getadhell.androidapp.fragments;

import android.Manifest;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.getadhell.androidapp.R;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Patrick.Lower on 1/17/2017.
 */

public class AppListFragment extends Fragment {
    private static final String TAG = AppListFragment.class.getCanonicalName();
    private ListView appListView;
    private List<ApplicationInfo> masterAppInfo;
    private String APPLIST = "applist.json";
    private Boolean onWhiteList = false;
    private IconAppAdapter iconAdapter;
    private Context context;
    private PackageManager packageManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.context = getActivity().getApplicationContext();
        packageManager = this.context.getPackageManager();
        final View view = inflater.inflate(R.layout.app_list_fragment, container, false);

        appListView = (ListView) view.findViewById(R.id.appList);
        new AdhellGetListTask().execute(false);

        final Button editButton = (Button) view.findViewById(R.id.whitelist);
        editButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d(TAG, "Whitelist button click in AppListFragment");
                if (onWhiteList) {
                    onWhiteList = false;
                    editButton.setText(R.string.whitelist);
                    setData(masterAppInfo);
                    iconAdapter.notifyDataSetChanged();
                    //new AdhellGetListTask().execute(false);
                } else {
                    onWhiteList = true;
                    editButton.setText(R.string.applist);
                    new AdhellGetWhiteListTask().execute(false);
                }
            }

        });

        appListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                if (onWhiteList) {
                    //remove from whitelist
                    //removeFromWhiteList(item);
                    new RemoveFromWhiteList().execute(item);
                    new AdhellGetWhiteListTask().execute(false);
                } else {
                    //add to whitelist
                    addToWhiteList(item);
                    //new AdhellGetListTask().execute(false);
                    iconAdapter.remove(item);
                    iconAdapter.notifyDataSetChanged();
                }
            }
        });

        Button back = (Button) view.findViewById(R.id.back_to_main);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Back button click in AppListFragment");
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainer, new BlockerFragment());
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    class IconAppAdapter extends BaseAdapter {
        private List<ApplicationInfo> applicationInfoList;

        public IconAppAdapter(List<ApplicationInfo> applicationInfoList) {
            this.applicationInfoList = applicationInfoList;
        }

        @Override
        public int getCount() {
            return this.applicationInfoList.size();
        }

        @Override
        public String getItem(int position) {
            return this.applicationInfoList.get(position).packageName;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(context);
            View row = inflater.inflate(R.layout.app_lits_view_item, parent, false);
            TextView appNameTextView = (TextView) row.findViewById(R.id.appName);
            ImageView appIconImageView = (ImageView) row.findViewById(R.id.appIcon);
            appNameTextView.setText(packageManager.getApplicationLabel(this.applicationInfoList.get(position)));
            appIconImageView.setImageDrawable(packageManager.getApplicationIcon(this.applicationInfoList.get(position)));
            return row;
        }

        public void remove(String packageName) {
            try {
                ApplicationInfo ai = packageManager.getApplicationInfo(packageName, 0);
                for (int i = 0; i < this.applicationInfoList.size(); i++){
                    if (this.applicationInfoList.get(i).packageName.equals(packageName)) {
                        this.applicationInfoList.remove(i);
                        break;
                    }
                }
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            masterAppInfo = this.applicationInfoList;
        }
    }


    private void setData(List<ApplicationInfo> data) {
        iconAdapter = new IconAppAdapter(data);
        appListView.setAdapter(iconAdapter);
    }

    private class AdhellGetWhiteListTask extends AsyncTask<Boolean, Void, List<ApplicationInfo>> {

        protected void onPreExecute() {

        }

        protected List<ApplicationInfo> doInBackground(Boolean... switchers) {
            List<String> appWhiteList = getWhiteList();
            List<ApplicationInfo> applicationInfoList = new ArrayList<>();
            for (String packageName : appWhiteList) {
                try {
                    applicationInfoList.add(packageManager.getApplicationInfo(packageName, 0));
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
            }

            return applicationInfoList;
        }

        protected void onPostExecute(List<ApplicationInfo> result) {
            setData(result);
        }
    }

    private class AdhellGetListTask extends AsyncTask<Boolean, Void, List<ApplicationInfo>> {
        ProgressDialog pd;

        protected void onPreExecute() {
            pd = ProgressDialog.show(getActivity(), "", "Please Wait, Loading Application List", false);
        }

        protected List<ApplicationInfo> doInBackground(Boolean... switchers) {
            PackageManager packageManager = getActivity().getPackageManager();
            List<String> appWhiteList = getWhiteList();
            final List<ApplicationInfo> pkgAppsList = packageManager.getInstalledApplications(0);
            Log.i(TAG, "Number of applications installed: " + pkgAppsList.size());
            Iterator<ApplicationInfo> applicationInfoIterator = pkgAppsList.iterator();
            while (applicationInfoIterator.hasNext()) {
                ApplicationInfo ai = applicationInfoIterator.next();
                int permissionState = packageManager.checkPermission(Manifest.permission.INTERNET, ai.packageName);
                if (permissionState != PackageManager.PERMISSION_GRANTED
                        || appWhiteList.contains(ai.packageName)) {
                    applicationInfoIterator.remove();
                }
            }
            Log.i(TAG, "Number of applications with INTERNET GRANDTED permission installed: " + pkgAppsList.size());
            Collections.sort(pkgAppsList, new ApplicationInfoNameComparator());
            return pkgAppsList;
        }

        protected void onPostExecute(List<ApplicationInfo> result) {
            pd.dismiss();
            masterAppInfo = result;
            setData(result);
        }
    }

    private class RemoveFromWhiteList extends AsyncTask<String, Void, Void> {
        ProgressDialog pd;

        protected void onPreExecute() {
            pd = ProgressDialog.show(getActivity(), "", "Please Wait, Removing App From Whitelist", false);
        }

        @Override
        protected Void doInBackground(String... params) {
            ArrayList<String> whitelist = getWhiteList();
            whitelist.remove(params[0]);
            File file;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                file = new File(getContext().getFilesDir(), APPLIST);
            } else {
                file = new File(getActivity().getFilesDir(), APPLIST);
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
            try {
                ApplicationInfo ai = packageManager.getApplicationInfo(params[0], 0);
                masterAppInfo.add(ai);
                Collections.sort(masterAppInfo, new Comparator<ApplicationInfo>() {
                    public int compare(ApplicationInfo ai1, ApplicationInfo ai2) {
                        String s1 = ai1.loadLabel(packageManager).toString();
                        String s2 = ai2.loadLabel(packageManager).toString();
                        return s1.compareTo(s2);
                    }
                });
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            pd.dismiss();
            return null;
        }

        protected void onPostExecute(Void result) {
            pd.dismiss();
        }
    }

    private void removeFromWhiteList(String url) {
        ProgressDialog pd = ProgressDialog.show(getActivity(), "", "Please Wait, Removing from Whitelist", false);
        ArrayList<String> whitelist = getWhiteList();
        whitelist.remove(url);
        File file;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            file = new File(getContext().getFilesDir(), APPLIST);
        } else {
            file = new File(getActivity().getFilesDir(), APPLIST);
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
        try {
            ApplicationInfo ai = packageManager.getApplicationInfo(url, 0);
            masterAppInfo.add(ai);
            Collections.sort(masterAppInfo, new Comparator<ApplicationInfo>() {
                public int compare(ApplicationInfo ai1, ApplicationInfo ai2) {
                    String s1 = ai1.loadLabel(packageManager).toString();
                    String s2 = ai2.loadLabel(packageManager).toString();
                    return s1.compareTo(s2);
                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        pd.dismiss();
    }

    private void addToWhiteList(String url) {
        ArrayList<String> whitelist = getWhiteList();
        whitelist.add(url);
        File file;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            file = new File(getContext().getFilesDir(), APPLIST);
        } else {
            file = new File(getActivity().getFilesDir(), APPLIST);
        }
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            Writer output = new BufferedWriter(new FileWriter(file));
            Gson gson = new Gson();
            output.write(gson.toJson(whitelist));
            output.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ArrayList<String> getWhiteList() {
        File file;
        ArrayList<String> whitelist = new ArrayList<String>();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            file = new File(getContext().getFilesDir(), APPLIST);
        } else {
            file = new File(getActivity().getFilesDir(), APPLIST);
        }
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                Gson gson = new Gson();
                whitelist = gson.fromJson(reader, ArrayList.class);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return whitelist;
    }


    private class ApplicationInfoNameComparator implements Comparator<ApplicationInfo> {
        @Override
        public int compare(ApplicationInfo lhs, ApplicationInfo rhs) {
            String lAppName = (String) packageManager.getApplicationLabel(lhs);
            String rAppName = (String) packageManager.getApplicationLabel(rhs);
            if (lAppName == null) {
                lAppName = "(Unknown)";
            }
            if (rAppName == null) {
                rAppName = "(Unknown)";
            }
            return lAppName.compareToIgnoreCase(rAppName);
        }
    }
}
