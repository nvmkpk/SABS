package com.getadhell.androidapp.utils;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Patrick.Lower on 1/19/2017.
 */

public class CustomArrayAdapter extends ArrayAdapter<String> {
    private List<String> originalList;
    private List<String> values;
    private CustomFilter filter;

    public CustomArrayAdapter(Context context, int layout, List<String> values) {
        super(context, layout, values);
        this.values = new ArrayList<String>();
        this.values.addAll(values);
        originalList = new ArrayList<String>();
        originalList.addAll(values);
    }

    @Override
    public Filter getFilter() {
        if (filter == null) {
            filter = new CustomFilter();
        }
        return filter;
    }

    private class CustomFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            constraint = constraint.toString().toLowerCase();
            FilterResults result = new FilterResults();
            if (constraint != null && constraint.toString().length() > 0) {
                ArrayList<String> filteredItems = new ArrayList<String>();
                for (String s : values) {
                    if (s.toString().toLowerCase().contains(constraint))
                        filteredItems.add(s);
                }
                result.count = filteredItems.size();
                result.values = filteredItems;
            } else {
                synchronized (this) {
                    result.values = originalList;
                    result.count = originalList.size();
                }
            }
            return result;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            values = (ArrayList<String>) results.values;
            notifyDataSetChanged();
            clear();
            for (int i = 0, l = values.size(); i < l; i++)
                add(values.get(i));
            notifyDataSetInvalidated();
        }
    }
}
