package com.getadhell.androidapp.model;

import java.util.ArrayList;
import java.util.List;

public class AdhellPermissionInfo {
    public final String name;
    public final String label;

    public AdhellPermissionInfo(String name, String label) {
        this.name = name;
        this.label = label;

    }

    public static List<AdhellPermissionInfo> loadPermissions() {
        AdhellPermissionInfo internetAdhellPermissionInfo = new AdhellPermissionInfo("android.permission.INTERNET", "Internet");
        AdhellPermissionInfo accessFineLocationAdhellPermissionInfo = new AdhellPermissionInfo("android.permission.ACCESS_FINE_LOCATION", "GPS Sensor");
        AdhellPermissionInfo cameraAdhellPermissionInfo = new AdhellPermissionInfo("android.permission.CAMERA", "Camera");
        AdhellPermissionInfo micAdhellPermissionInfo = new AdhellPermissionInfo("android.permission.RECORD_AUDIO", "Microphone");
        List<AdhellPermissionInfo> lis = new ArrayList<>();
        lis.add(internetAdhellPermissionInfo);
        lis.add(accessFineLocationAdhellPermissionInfo);
        lis.add(cameraAdhellPermissionInfo);
        lis.add(micAdhellPermissionInfo);
        return lis;
    }
}
