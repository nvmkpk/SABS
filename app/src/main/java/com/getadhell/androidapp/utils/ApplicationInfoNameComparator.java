package com.getadhell.androidapp.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.Comparator;

public class ApplicationInfoNameComparator implements Comparator<ApplicationInfo> {
    PackageManager packageManager;

    public ApplicationInfoNameComparator(PackageManager pkgManager) {
        this.packageManager = pkgManager;
    }

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
