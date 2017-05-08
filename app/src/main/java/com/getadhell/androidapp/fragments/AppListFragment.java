package com.getadhell.androidapp.fragments;

import android.Manifest;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;

import com.getadhell.androidapp.R;
import com.getadhell.androidapp.utils.AppWhiteList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;


public class AppListFragment extends Fragment {
    private static final String TAG = AppListFragment.class.getCanonicalName();
    private ListView appListView;
    private List<ApplicationInfo> masterAppInfo;
    private Boolean onWhiteList = false;
    private IconAppAdapter iconAdapter;
    private Context context;
    private PackageManager packageManager;
    private List<AsyncTask> runningTaskList = new ArrayList<>();
    private AppWhiteList mAppWhiteList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAppWhiteList = new AppWhiteList();
        this.context = getActivity().getApplicationContext();
        packageManager = this.context.getPackageManager();
        final View view = inflater.inflate(R.layout.fragment_app_list, container, false);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appListView = (ListView) view.findViewById(R.id.appList);
        new AdhellGetListTask().execute(false);

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
                    mAppWhiteList.addToWhiteList(item);
                    //new AdhellGetListTask().execute(false);
                    iconAdapter.remove(item);
                    iconAdapter.notifyDataSetChanged();
                }
            }
        });

        TabHost th = (TabHost) view.findViewById(R.id.urlTabHost);
        th.setup();
        th.addTab(th.newTabSpec("BlockTab").setIndicator(getResources().getString(R.string.block_app_tab_label)).setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                return new View(getActivity());
            }
        }));
        th.addTab(th.newTabSpec("AllowTab").setIndicator(getResources().getString(R.string.allowed_app_tab_label)).setContent(new TabHost.TabContentFactory() {
            @Override
            public View createTabContent(String tag) {
                return new View(getActivity());
            }
        }));

        th.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                if (tabId.equals("BlockTab")) {
                    onWhiteList = false;
                    setData(masterAppInfo);
                    iconAdapter.notifyDataSetChanged();
                }
                if (tabId.equals("AllowTab")) {
                    onWhiteList = true;
                    new AdhellGetWhiteListTask().execute(false);
                }
            }
        });

        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        for (AsyncTask task : runningTaskList) {
            if (task != null && task.getStatus() == AsyncTask.Status.RUNNING)
                task.cancel(true);
        }
    }

    private void setData(List<ApplicationInfo> data) {
        iconAdapter = new IconAppAdapter(data);
        appListView.setAdapter(iconAdapter);
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
            View row = inflater.inflate(R.layout.item_app_lits_view, parent, false);
            TextView appNameTextView = (TextView) row.findViewById(R.id.appName);
            ImageView appIconImageView = (ImageView) row.findViewById(R.id.appIcon);
            appNameTextView.setText(packageManager.getApplicationLabel(this.applicationInfoList.get(position)));
            appIconImageView.setImageDrawable(packageManager.getApplicationIcon(this.applicationInfoList.get(position)));
            return row;
        }

        public void remove(String packageName) {
            for (int i = 0; i < this.applicationInfoList.size(); i++) {
                if (this.applicationInfoList.get(i).packageName.equals(packageName)) {
                    this.applicationInfoList.remove(i);
                    break;
                }
            }
            masterAppInfo = this.applicationInfoList;
        }
    }

    private class AdhellGetWhiteListTask extends AsyncTask<Boolean, Void, List<ApplicationInfo>> {

        protected void onPreExecute() {
            runningTaskList.add(this);
        }

        protected List<ApplicationInfo> doInBackground(Boolean... switchers) {
            List<String> appWhiteList = mAppWhiteList.getWhiteList();
            List<ApplicationInfo> applicationInfoList = new ArrayList<>();
            if (appWhiteList == null || appWhiteList.size() == 0) {
                return applicationInfoList;
            }
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
            if (!isCancelled())
                setData(result);
            runningTaskList.remove(this);
        }
    }

    private class AdhellGetListTask extends AsyncTask<Boolean, Void, List<ApplicationInfo>> {
        ProgressDialog pd;

        protected void onPreExecute() {
            runningTaskList.add(this);
            pd = ProgressDialog.show(getActivity(), "", "Please Wait, Loading Application List", false);
        }

        protected List<ApplicationInfo> doInBackground(Boolean... switchers) {
            PackageManager packageManager = getActivity().getPackageManager();
            List<String> appWhiteList = mAppWhiteList.getWhiteList();
            final List<ApplicationInfo> pkgAppsList = packageManager.getInstalledApplications(0);
            Log.i(TAG, "Number of applications installed: " + pkgAppsList.size());
            Iterator<ApplicationInfo> applicationInfoIterator = pkgAppsList.iterator();
            while (applicationInfoIterator.hasNext()) {
                ApplicationInfo ai = applicationInfoIterator.next();
                int permissionState = packageManager.checkPermission(Manifest.permission.INTERNET, ai.packageName);
                if (permissionState != PackageManager.PERMISSION_GRANTED
                        || (appWhiteList != null && appWhiteList.contains(ai.packageName))) {
                    applicationInfoIterator.remove();
                }
            }
            Log.i(TAG, "Number of applications with INTERNET GRANDTED permission installed: " + pkgAppsList.size());
            Collections.sort(pkgAppsList, new ApplicationInfoNameComparator());
            return pkgAppsList;
        }

        protected void onPostExecute(List<ApplicationInfo> result) {
            pd.dismiss();
            if (!isCancelled()) {
                masterAppInfo = result;
                setData(result);
            }
            runningTaskList.remove(this);
        }
    }

    private class RemoveFromWhiteList extends AsyncTask<String, Void, Void> {
        ProgressDialog pd;

        protected void onPreExecute() {
            runningTaskList.add(this);
            pd = ProgressDialog.show(getActivity(), "", "Please Wait, Removing App From Whitelist", false);
        }

        @Override
        protected Void doInBackground(String... params) {
            mAppWhiteList.removeFromWhiteList(params[0]);
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
            runningTaskList.remove(this);
        }
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
