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
        AdhellPermissionInfo cameraAdhellPermissionInfo = new AdhellPermissionInfo("android.permission.CAMERA", "Camera");
        AdhellPermissionInfo micAdhellPermissionInfo = new AdhellPermissionInfo("android.permission.RECORD_AUDIO", "Microphone");
        AdhellPermissionInfo wakeLockPerm = new AdhellPermissionInfo("android.permission.WAKE_LOCK", "Wake Lock");

        List<AdhellPermissionInfo> lis = new ArrayList<>();
        lis.add(internetAdhellPermissionInfo);
        lis.add(cameraAdhellPermissionInfo);
        lis.add(micAdhellPermissionInfo);
        lis.add(wakeLockPerm);
        lis.add(new AdhellPermissionInfo("android.permission.SEND_SMS", "Send SMS"));
        lis.add(new AdhellPermissionInfo("android.permission.RECEIVE_SMS", "Receive SMS"));
        lis.add(new AdhellPermissionInfo("android.permission.RECEIVE_MMS", "Receive MMS"));
        lis.add(new AdhellPermissionInfo("android.permission.ACCESS_WIFI_STATE", "Access info about Wi-Fi networks"));
        lis.add(new AdhellPermissionInfo("android.permission.BATTERY_STATS", "Battery statistics"));
        lis.add(new AdhellPermissionInfo("android.permission.NFC", "NFC"));
        lis.add(new AdhellPermissionInfo("android.permission.RECEIVE_BOOT_COMPLETED", "Receive Boot Completed"));
        lis.add(new AdhellPermissionInfo("android.permission.BLUETOOTH", "Bluetooth"));
        lis.add(new AdhellPermissionInfo("android.permission.BLUETOOTH_ADMIN", "Bluetooth Admin"));

        lis.add(new AdhellPermissionInfo("android.permission.USE_FINGERPRINT", "Fingerprint"));
        lis.add(new AdhellPermissionInfo("android.permission.VIBRATE", "Vibrate"));
        lis.add(new AdhellPermissionInfo("android.permission.ACCESS_NETWORK_STATE", "Access information about networks"));

        // dangerous
        lis.add(new AdhellPermissionInfo("android.permission.ACCESS_COARSE_LOCATION", "Access approximate location"));
        lis.add(new AdhellPermissionInfo("android.permission.ACCESS_FINE_LOCATION", "Access precise location"));
        lis.add(new AdhellPermissionInfo("android.permission.ANSWER_PHONE_CALLS", "Answer phone calls"));
        lis.add(new AdhellPermissionInfo("android.permission.CALL_PHONE", "Call without prompt"));
        lis.add(new AdhellPermissionInfo("android.permission.BODY_SENSORS", "Body sensors"));
        lis.add(new AdhellPermissionInfo("android.permission.GET_ACCOUNTS", "Allows access to the list of accounts in the Accounts Service"));
        lis.add(new AdhellPermissionInfo("android.permission.GET_ACCOUNTS_PRIVILEGED", "Allows access to the list of accounts in the Accounts Service"));
        lis.add(new AdhellPermissionInfo("android.permission.PROCESS_OUTGOING_CALLS", "Process outgoing calls"));
        lis.add(new AdhellPermissionInfo("android.permission.READ_CALENDAR", "Read calendar"));
        lis.add(new AdhellPermissionInfo("android.permission.READ_CALL_LOG", "Read call log"));
        lis.add(new AdhellPermissionInfo("android.permission.READ_CONTACTS", "Read Contacts"));
        lis.add(new AdhellPermissionInfo("android.permission.WRITE_CONTACTS", "Write Contacts"));

        // normal
        lis.add(new AdhellPermissionInfo("android.permission.ACCESS_LOCATION_EXTRA_COMMANDS", "Access extra location provider commands"));
        lis.add(new AdhellPermissionInfo("android.permission.GET_PACKAGE_SIZE", "Find out the space used by any package"));
        lis.add(new AdhellPermissionInfo("android.permission.READ_PHONE_STATE", "Allows read access to phone state (phone number, current network info)"));


        return lis;
    }
}
