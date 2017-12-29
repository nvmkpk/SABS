package com.layoutxml.sabs.fragments;

import android.annotation.SuppressLint;
import android.app.enterprise.ApplicationPolicy;
import android.arch.lifecycle.LifecycleFragment;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.layoutxml.sabs.App;
import com.layoutxml.sabs.BuildConfig;
import com.layoutxml.sabs.R;
import com.layoutxml.sabs.db.AppDatabase;
import com.layoutxml.sabs.db.entity.AppInfo;
import com.layoutxml.sabs.utils.AppsListDBInitializer;

import java.util.List;
import java.io.*;

import javax.inject.Inject;

public class PackageDisablerFragment extends LifecycleFragment {
    private static final String TAG = PackageDisablerFragment.class.getCanonicalName();
    private final int SORTED_ALPHABETICALLY = 0;
    private final int SORTED_INSTALL_TIME = 1;
    private final int SORTED_DISABLED = 2;
    @Nullable
    @Inject
    ApplicationPolicy appPolicy;
    @Inject
    AppDatabase mDb;
    @Inject
    PackageManager packageManager;
    private ListView installedAppsView;
    private Context context;
    private List<AppInfo> packageList;
    private DisablerAppAdapter adapter;
    private EditText editText;
    private int sortState = SORTED_ALPHABETICALLY;
    private AppCompatActivity parentActivity;


    public PackageDisablerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.get().getAppComponent().inject(this);
        parentActivity = (AppCompatActivity) getActivity();
        if (BuildConfig.APPLICATION_ID!="com.layoutxml.sabs") {
            throw new RuntimeException("Administrative permissions not granted");
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.package_disabler_fragment_title));
        if (parentActivity.getSupportActionBar() != null) {
            parentActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            parentActivity.getSupportActionBar().setHomeButtonEnabled(false);
        }
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.fragment_package_disabler, container, false);
        context = getActivity().getApplicationContext();
        editText = view.findViewById(R.id.disabledFilter);
        editText.setOnClickListener(v -> editText.setCursorVisible(true));

        installedAppsView = view.findViewById(R.id.installed_apps_list);
        installedAppsView.setOnItemClickListener((AdapterView<?> adView, View v, int i, long l) -> {
            DisablerAppAdapter disablerAppAdapter = (DisablerAppAdapter) adView.getAdapter();
            final String name = disablerAppAdapter.getItem(i).packageName;
            new AsyncTask<Void, Void, Boolean>() {
                @Override
                protected Boolean doInBackground(Void... o) {
                    AppInfo appInfo = mDb.applicationInfoDao().getByPackageName(name);
                    appInfo.disabled = !appInfo.disabled;
                    if (appInfo.disabled) appPolicy.setDisableApplication(name);
                    else appPolicy.setEnableApplication(name);
                    mDb.applicationInfoDao().insert(appInfo);
                    disablerAppAdapter.applicationInfoList.set(i, appInfo);
                    return appInfo.disabled;
                }

                @Override
                protected void onPostExecute(Boolean b) {
                    ((Switch) v.findViewById(R.id.switchDisable)).setChecked(!b);
                }
            }.execute();
        });

        loadApplicationsList(false);
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                loadApplicationsList(false);
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.package_disabler_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_pack_dis_sort:
                break;
            case R.id.sort_alphabetically_item:
                if (sortState == SORTED_ALPHABETICALLY) break;
                sortState = SORTED_ALPHABETICALLY;
                Toast.makeText(context, getString(R.string.app_list_sorted_by_alphabet), Toast.LENGTH_SHORT).show();
                loadApplicationsList(false);
                break;
            case R.id.sort_by_time_item:
                if (sortState == SORTED_INSTALL_TIME) break;
                sortState = SORTED_INSTALL_TIME;
                Toast.makeText(context, getString(R.string.app_list_sorted_by_date), Toast.LENGTH_SHORT).show();
                loadApplicationsList(false);
                break;
            case R.id.sort_disabled_item:
                sortState = SORTED_DISABLED;
                Toast.makeText(context, getString(R.string.app_list_sorted_by_disabled), Toast.LENGTH_SHORT).show();
                loadApplicationsList(false);
                break;
            case R.id.action_pack_dis_reload:
                editText.setText("");
                loadApplicationsList(true);
                break;
            case R.id.disabler_import_storage:
                Toast.makeText(context, getString(R.string.imported_from_storage), Toast.LENGTH_SHORT).show();
                importList();
                break;
            case R.id.disabler_export_storage:
                Toast.makeText(context, getString(R.string.exported_to_storage), Toast.LENGTH_SHORT).show();
                exportList();
                break;
            case R.id.disabler_enable_all:
                Toast.makeText(context, getString(R.string.enabled_all_disabled), Toast.LENGTH_SHORT).show();
                enableAllPackages();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    private void importList() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... o) {
                File file = new File(Environment.getExternalStorageDirectory(), "adhell_packages.txt");

                try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                    String line;

                    while ((line = reader.readLine()) != null) {
                        try {
                            AppInfo appInfo = mDb.applicationInfoDao().getByPackageName(line);
                            appInfo.disabled = true;
                            appPolicy.setDisableApplication(line);
                            mDb.applicationInfoDao().insert(appInfo);
                        }
                        catch (Exception e) {
                            // Ignore any potential errors
                        }
                    }
                }
                catch (IOException e) {
                    Log.e("Exception", "File write failed: " + e.toString());
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void o) {
                super.onPostExecute(o);
                loadApplicationsList(true);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void exportList() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... o) {
                File file = new File(Environment.getExternalStorageDirectory(), "adhell_packages.txt");
                List<AppInfo> disabledAppList = mDb.applicationInfoDao().getDisabledApps();

                try {
                    FileOutputStream stream = new FileOutputStream(file);
                    OutputStreamWriter writer = new OutputStreamWriter(stream);

                    writer.write("");

                    for (AppInfo app : disabledAppList) {
                        writer.append(app.packageName + "\n");
                    }

                    writer.close();
                    stream.flush();
                    stream.close();
                }
                catch (IOException e) {
                    Log.e("Exception", "File write failed: " + e.toString());
                }

                return null;
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void enableAllPackages() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... o) {
                List<AppInfo> disabledAppList = mDb.applicationInfoDao().getDisabledApps();

                for (AppInfo app : disabledAppList) {
                    app.disabled = false;
                    appPolicy.setEnableApplication(app.packageName);
                    mDb.applicationInfoDao().insert(app);
                }

                return null;
            }

            @Override
            protected void onPostExecute(Void o) {
                super.onPostExecute(o);
                loadApplicationsList(true);
            }
        }.execute();
    }

    @SuppressLint("StaticFieldLeak")
    private void loadApplicationsList(boolean clear) {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... o) {
                if (clear) mDb.applicationInfoDao().deleteAll();
                else {
                    packageList = getListFromDb();
                    if (packageList.size() != 0) return null;
                }
                AppsListDBInitializer.getInstance().fillPackageDb(packageManager);
                packageList = getListFromDb();
                return null;
            }

            @Override
            protected void onPostExecute(Void o) {
                super.onPostExecute(o);
                adapter = new DisablerAppAdapter(packageList);
                installedAppsView.setAdapter(adapter);
                installedAppsView.invalidateViews();
            }
        }.execute();
    }

    private List<AppInfo> getListFromDb() {
        String filterText = '%' + editText.getText().toString() + '%';
        switch (sortState) {
            case SORTED_ALPHABETICALLY:
                if (filterText.length() == 0) return mDb.applicationInfoDao().getAll();
                return mDb.applicationInfoDao().getAllAppsWithStrInName(filterText);
            case SORTED_INSTALL_TIME:
                if (filterText.length() == 0) return mDb.applicationInfoDao().getAllRecentSort();
                return mDb.applicationInfoDao().getAllAppsWithStrInNameTimeOrder(filterText);
            case SORTED_DISABLED:
                if (filterText.length() == 0)
                    return mDb.applicationInfoDao().getAllSortedByDisabled();
                return mDb.applicationInfoDao().getAllAppsWithStrInNameDisabledOrder(filterText);
        }
        return null;
    }

    public static class ViewHolder {
        TextView nameH;
        TextView packageH;
        Switch switchH;
        ImageView imageH;
    }

    private class DisablerAppAdapter extends BaseAdapter {
        public List<AppInfo> applicationInfoList;

        public DisablerAppAdapter(List<AppInfo> appInfoList) {
            applicationInfoList = appInfoList;
        }

        @Override
        public int getCount() {
            return this.applicationInfoList.size();
        }

        @Override
        public AppInfo getItem(int position) {
            return this.applicationInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_disable_app_list_view, parent, false);
                holder = new ViewHolder();
                holder.nameH = convertView.findViewById(R.id.appName);
                holder.packageH = convertView.findViewById(R.id.packName);
                holder.switchH = convertView.findViewById(R.id.switchDisable);
                holder.imageH = convertView.findViewById(R.id.appIcon);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            AppInfo appInfo = applicationInfoList.get(position);
            holder.nameH.setText(appInfo.appName);
            holder.packageH.setText(appInfo.packageName);
            holder.switchH.setChecked(!appInfo.disabled);
            if (appInfo.system) {
                convertView.findViewById(R.id.systemOrNot).setVisibility(View.VISIBLE);
            } else {
                convertView.findViewById(R.id.systemOrNot).setVisibility(View.GONE);
            }
            try {
                holder.imageH.setImageDrawable(packageManager.getApplicationIcon(appInfo.packageName));
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Failed to get ImageDrawable", e);
            }
            return convertView;
        }
    }
}
