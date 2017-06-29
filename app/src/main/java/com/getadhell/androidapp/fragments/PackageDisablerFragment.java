package com.getadhell.androidapp.fragments;

import android.support.v4.app.Fragment;
import android.app.ProgressDialog;
import android.app.enterprise.ApplicationPolicy;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
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

import com.getadhell.androidapp.R;
import com.getadhell.androidapp.utils.ApplicationInfoNameComparator;
import com.getadhell.androidapp.utils.DeviceUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PackageDisablerFragment extends Fragment {
    private ListView installedAppsView;
    private Context context;
    private PackageManager packageManager;
    private ApplicationPolicy appPolicy;
    private ProgressDialog pd;
    private List<ApplicationInfo> packageList;

    public PackageDisablerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle(getString(R.string.package_disabler_fragment_title));
        context = getActivity().getApplicationContext();
        appPolicy = DeviceUtils.getEnterpriseDeviceManager().getApplicationPolicy();
        packageManager = getActivity().getPackageManager();
        View view = inflater.inflate(R.layout.fragment_package_disabler, container, false);
        EditText editText = (EditText) view.findViewById(R.id.disabledFilter);
        packageList = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        DisablerAppAdapter adapter = new DisablerAppAdapter(packageList);
        view.findViewById(R.id.buttonFilterDisable).setOnClickListener(view1 ->
        {
            String text = editText.getText().toString();
            new AsyncTask<Boolean, Void, Void>() {
                protected void onPreExecute() {
                    pd = ProgressDialog.show(getActivity(), "", getString(R.string.applying_filter));
                }

                protected Void doInBackground(Boolean... switchers) {
                    adapter.getFilter().filter(text);
                    return null;
                }
            }.execute(false);
        });

        installedAppsView = (ListView) view.findViewById(R.id.installed_apps_list);
        installedAppsView.setAdapter(adapter);
        installedAppsView.invalidateViews();
        installedAppsView.setOnItemClickListener((AdapterView<?> adView, View v, int i, long l) ->
        {
            String name = ((DisablerAppAdapter) adView.getAdapter()).getItem(i).packageName;
            boolean enabled = appPolicy.getApplicationStateEnabled(name);
            if (enabled) appPolicy.setDisableApplication(name);
            else appPolicy.setEnableApplication(name);
            ((Switch) v.findViewById(R.id.switchDisable)).setChecked(!enabled);
        });

        return view;
    }

    public static class ViewHolder {
        TextView textH;
        Switch switchH;
        ImageView imageH;
    }

    private class DisablerAppAdapter extends BaseAdapter implements Filterable {
        private List<ApplicationInfo> applicationInfoList;
        private List<ApplicationInfo> filteredList = new ArrayList<>();
        private ItemFilter filter;

        public DisablerAppAdapter(List<ApplicationInfo> appInfoList) {
            applicationInfoList = new ArrayList<>();
            for (ApplicationInfo appInfo : appInfoList)
                if (!appInfo.packageName.equals("com.getadhell.androidapp"))
                    applicationInfoList.add(appInfo);
            filter = new ItemFilter();
            doSort();
        }

        private void doSort() {
            new AsyncTask<Boolean, Void, Void>() {
                protected void onPreExecute() {
                    pd = ProgressDialog.show(getActivity(), "", "Please Wait, Sorting Applications");
                }

                protected Void doInBackground(Boolean... switchers) {
                    Collections.sort(applicationInfoList, new ApplicationInfoNameComparator(packageManager));
                    return null;
                }

                protected void onPostExecute(Void result) {
                    pd.dismiss();
                    filteredList = applicationInfoList;
                    notifyDataSetChanged();
                }
            }.execute(false);
        }

        @Override
        public int getCount() {
            return this.filteredList.size();
        }

        @Override
        public ApplicationInfo getItem(int position) {
            return this.filteredList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public Filter getFilter() {
            return filter;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_disable_app_list_view, parent, false);
                holder = new ViewHolder();
                holder.textH = (TextView) convertView.findViewById(R.id.appName);
                holder.switchH = (Switch) convertView.findViewById(R.id.switchDisable);
                holder.imageH = (ImageView) convertView.findViewById(R.id.appIcon);
                convertView.setTag(holder);
            } else holder = (ViewHolder) convertView.getTag();

            ApplicationInfo appInfo = this.filteredList.get(position);
            holder.textH.setText(packageManager.getApplicationLabel(appInfo));
            holder.switchH.setChecked(appPolicy.getApplicationStateEnabled(appInfo.packageName));
            holder.imageH.setImageDrawable(packageManager.getApplicationIcon(appInfo));

            return convertView;
        }

        private class ItemFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String filterString = constraint.toString().toLowerCase();
                FilterResults results = new FilterResults();
                final ArrayList<ApplicationInfo> nlist = new ArrayList<>();
                for (ApplicationInfo appInfo : applicationInfoList)
                    if (packageManager.getApplicationLabel(appInfo).toString().toLowerCase().contains(filterString))
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
                filteredList = (List<ApplicationInfo>) results.values;
                notifyDataSetChanged();
            }
        }
    }
}
