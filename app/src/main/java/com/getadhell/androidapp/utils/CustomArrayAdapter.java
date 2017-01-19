package com.getadhell.androidapp.utils;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;

import java.util.ArrayList;

/**
 * Created by Patrick.Lower on 1/19/2017.
 */

public class CustomArrayAdapter extends ArrayAdapter<String> {
    private ArrayList<String> originalList;
    private ArrayList<String> Values;
    private CustomFilter filter;

    public CustomArrayAdapter(Context context, int layout, ArrayList<String> values) {
        super(context, layout, values);
        Values = new ArrayList<String>();
        Values.addAll(values);
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
                for (String s : Values) {
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
            Values = (ArrayList<String>)results.values;
            notifyDataSetChanged();
            clear();
            for(int i = 0, l = Values.size(); i < l; i++)
                add(Values.get(i));
            notifyDataSetInvalidated();
        }
    }
}
