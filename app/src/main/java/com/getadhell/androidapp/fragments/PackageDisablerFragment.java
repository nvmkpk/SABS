package com.getadhell.androidapp.fragments;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.app.enterprise.ApplicationPolicy;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.R;
import com.getadhell.androidapp.db.AppDatabase;
import com.getadhell.androidapp.db.entity.AppInfo;
import com.getadhell.androidapp.utils.ApplicationInfoNameComparator;
import com.getadhell.androidapp.utils.AppsListDBInitializer;
import com.getadhell.androidapp.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PackageDisablerFragment extends Fragment
{
    private ListView installedAppsView;
    private Context context;
    private PackageManager packageManager;
    private ApplicationPolicy appPolicy;
    private ProgressDialog pd;
    private List<AppInfo> packageList;
    private DisablerAppAdapter adapter;
    private AppDatabase mDb;
    private EditText editText;
    private boolean sortLexic = true;

    public PackageDisablerFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        getActivity().setTitle(getString(R.string.package_disabler_fragment_title));
        View view = inflater.inflate(R.layout.fragment_package_disabler, container, false);
        context = getActivity().getApplicationContext();
        packageManager = getActivity().getPackageManager();
        editText = (EditText) view.findViewById(R.id.disabledFilter);
        setHasOptionsMenu(true);

        appPolicy = DeviceUtils.getEnterpriseDeviceManager().getApplicationPolicy();
        installedAppsView = (ListView) view.findViewById(R.id.installed_apps_list);
        installedAppsView.setOnItemClickListener((AdapterView<?> adView, View v, int i, long l) ->
        {
            String name = ((DisablerAppAdapter) adView.getAdapter()).getItem(i).packageName;
            boolean enabled = appPolicy.getApplicationStateEnabled(name);
            if (enabled) appPolicy.setDisableApplication(name);
            else appPolicy.setEnableApplication(name);
            ((Switch) v.findViewById(R.id.switchDisable)).setChecked(!enabled);
        });

        loadApplicationsList(false);

        editText.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) { loadApplicationsList(false); }
        });

        /*view.findViewById(R.id.buttonFilterDisable).setOnClickListener(view1 ->
        {
            loadApplicationsList(false);
            /*
            new AsyncTask<Boolean, Void, Void>() {
                protected void onPreExecute() {
                    pd = ProgressDialog.show(getActivity(), "", getString(R.string.applying_filter));
                }

                protected Void doInBackground(Boolean... switchers) {
                    if (adapter != null) adapter.getFilter().filter(text);
                    return null;
                }
            }.execute(false);
        });*/

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.package_disabler_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_pack_dis_sort:
                sortLexic = !sortLexic;
                if (sortLexic)
                {
                    item.setIcon(getResources().getDrawable(R.drawable.date_sort));
                    Toast.makeText(context, getString(R.string.app_list_sorted_by_alphabet), Toast.LENGTH_SHORT).show();
                    loadApplicationsList(false);
                } else
                {
                    item.setIcon(getResources().getDrawable(R.drawable.alpha_sort));
                    Toast.makeText(context, getString(R.string.app_list_sorted_by_date), Toast.LENGTH_SHORT).show();
                    loadApplicationsList(false);
                }
                break;
            case R.id.action_pack_dis_reload:
                editText.setText("");
                loadApplicationsList(true);
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    private void loadApplicationsList(boolean clear)
    {
        new AsyncTask<Void, Void, Void>()
        {
            @Override
            protected Void doInBackground(Void... o)
            {
                mDb = AppDatabase.getAppDatabase(App.get().getApplicationContext());
                if (clear) mDb.applicationInfoDao().deleteAll();
                else
                {
                    packageList = getListFromDb();
                    if (packageList.size() != 0) return null;
                }
                AppsListDBInitializer.getInstance().fillPackageDb(packageManager);
                packageList = getListFromDb();
                return null;
            }

            @Override
            protected void onPostExecute(Void o)
            {
                super.onPostExecute(o);
                adapter = new DisablerAppAdapter(packageList);
                installedAppsView.setAdapter(adapter);
                installedAppsView.invalidateViews();
            }
        }.execute();
    }

    private List<AppInfo> getListFromDb()
    {
        String filterText = editText.getText().toString();
        if (filterText.length() == 0)
        {
            if (sortLexic) return mDb.applicationInfoDao().getAll();
            return mDb.applicationInfoDao().getAllRecentSort();
        }
        else
        {
            if (sortLexic) return mDb.applicationInfoDao().getAllAppsWithStrInName('%' + filterText + '%');
            return mDb.applicationInfoDao().getAllAppsWithStrInNameTimeOrder('%' + filterText + '%');
        }
    }

    public static class ViewHolder
    {
        TextView nameH;
        TextView packageH;
        Switch switchH;
        ImageView imageH;
    }

    private class DisablerAppAdapter extends BaseAdapter// implements Filterable
    {
        private List<AppInfo> applicationInfoList;
        //private List<AppInfo> filteredList = new ArrayList<>();
        //private ItemFilter filter;

        public DisablerAppAdapter(List<AppInfo> appInfoList)
        {
            applicationInfoList = appInfoList;
            /*for (AppInfo appInfo : appInfoList)
                if (!appInfo.packageName.equals("com.getadhell.androidapp"))
                    applicationInfoList.add(appInfo);
            //filter = new ItemFilter();
            filteredList = applicationInfoList;*/
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

        /*@Override
        public Filter getFilter() {
            return filter;
        }*/

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_disable_app_list_view, parent, false);
                holder = new ViewHolder();
                holder.nameH = (TextView) convertView.findViewById(R.id.appName);
                holder.packageH = (TextView) convertView.findViewById(R.id.packName);
                holder.switchH = (Switch) convertView.findViewById(R.id.switchDisable);
                holder.imageH = (ImageView) convertView.findViewById(R.id.appIcon);
                convertView.setTag(holder);
            } else holder = (ViewHolder) convertView.getTag();

            AppInfo appInfo = applicationInfoList.get(position);
            holder.nameH.setText(appInfo.appName);
            holder.packageH.setText(appInfo.packageName);
            holder.switchH.setChecked(appPolicy.getApplicationStateEnabled(appInfo.packageName));
            if (appInfo.system) convertView.findViewById(R.id.systemOrNot).setVisibility(View.VISIBLE);
            else convertView.findViewById(R.id.systemOrNot).setVisibility(View.GONE);
            try { holder.imageH.setImageDrawable(packageManager.getApplicationIcon(appInfo.packageName)); }
            catch (PackageManager.NameNotFoundException e) {}

            //holder.textH.setText(packageManager.getApplicationLabel(appInfo));
            //holder.switchH.setChecked(appPolicy.getApplicationStateEnabled(appInfo.packageName));
            //holder.imageH.setImageDrawable(packageManager.getApplicationIcon(appInfo));

            return convertView;
        }

        /*private class ItemFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterString = constraint.toString().toLowerCase();
                FilterResults results = new FilterResults();
                final ArrayList<AppInfo> nlist = new ArrayList<>();
                for (AppInfo appInfo : applicationInfoList)
                    if (appInfo.appName.toLowerCase().contains(filterString))
                        nlist.add(appInfo);

                results.values = nlist;
                results.count = nlist.size();
                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (pd != null) pd.dismiss();
                if (results.values == null) return;
                filteredList = (List<AppInfo>) results.values;
                notifyDataSetChanged();
            }
        }*/
    }
}
