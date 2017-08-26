package com.fiendfyre.AdHell2.model;

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

        List<AdhellPermissionInfo> lis = new ArrayList<>();
        lis.add(new AdhellPermissionInfo("android.permission.INTERNET", "Internet"));
        lis.add(new AdhellPermissionInfo("android.permission.CAMERA", "Access the camera device"));
        lis.add(new AdhellPermissionInfo("android.permission.RECORD_AUDIO", "Record audio"));
        lis.add(new AdhellPermissionInfo("android.permission.WAKE_LOCK", "Allows using PowerManager WakeLocks to keep processor from sleeping or screen from dimming"));
        lis.add(new AdhellPermissionInfo("android.permission.SEND_SMS", "Send SMS messages"));
        lis.add(new AdhellPermissionInfo("android.permission.RECEIVE_SMS", "Receive SMS messages"));
        lis.add(new AdhellPermissionInfo("android.permission.RECEIVE_MMS", "Monitor incoming MMS messages"));
        lis.add(new AdhellPermissionInfo("android.permission.ACCESS_WIFI_STATE", "Access information about Wi-Fi networks"));
        lis.add(new AdhellPermissionInfo("android.permission.BATTERY_STATS", "Collect battery statistics"));
        lis.add(new AdhellPermissionInfo("android.permission.BLUETOOTH", "Connect to paired bluetooth devices"));
        lis.add(new AdhellPermissionInfo("android.permission.BLUETOOTH_ADMIN", "Discover and pair bluetooth devices"));
        lis.add(new AdhellPermissionInfo("android.permission.USE_FINGERPRINT", "Use fingerprint hardware"));
        lis.add(new AdhellPermissionInfo("android.permission.VIBRATE", "Access to the vibrator"));
        lis.add(new AdhellPermissionInfo("android.permission.ACCESS_NETWORK_STATE", "Access information about networks"));

        // dangerous
        lis.add(new AdhellPermissionInfo("android.permission.ACCESS_COARSE_LOCATION", "Access approximate location"));
        lis.add(new AdhellPermissionInfo("android.permission.ACCESS_FINE_LOCATION", "Access precise location"));
        lis.add(new AdhellPermissionInfo("android.permission.ANSWER_PHONE_CALLS", "Answer an incoming phone call"));
        lis.add(new AdhellPermissionInfo("android.permission.CALL_PHONE", "Initiate a phone call without going through the Dialer user interface for the user to confirm the call"));
        lis.add(new AdhellPermissionInfo("android.permission.BODY_SENSORS", "Access data from sensors that the user uses to measure what is happening inside his/her body, such as heart rate"));
        lis.add(new AdhellPermissionInfo("android.permission.GET_ACCOUNTS", "Allows access to the list of accounts in the Accounts Service"));
        lis.add(new AdhellPermissionInfo("android.permission.GET_ACCOUNTS_PRIVILEGED", "Allows access to the list of accounts in the Accounts Service (Since 6.0)"));
        lis.add(new AdhellPermissionInfo("android.permission.PROCESS_OUTGOING_CALLS", "See the number being dialed during an outgoing call with the option to redirect the call to a different number or abort the call altogether"));
        lis.add(new AdhellPermissionInfo("android.permission.READ_CALENDAR", "Read the user's calendar data"));
        lis.add(new AdhellPermissionInfo("android.permission.WRITE_CALENDAR", "Write the user's calendar data"));
        lis.add(new AdhellPermissionInfo("android.permission.READ_CALL_LOG", "Read the user's call log"));
        lis.add(new AdhellPermissionInfo("android.permission.WRITE_CALL_LOG", "Write (but not read) the user's call log data"));
        lis.add(new AdhellPermissionInfo("android.permission.READ_CONTACTS", "Read the user's contacts data"));
        lis.add(new AdhellPermissionInfo("android.permission.WRITE_CONTACTS", "Write the user's contacts data"));
        lis.add(new AdhellPermissionInfo("android.permission.READ_EXTERNAL_STORAGE", "Read from external storage"));
        lis.add(new AdhellPermissionInfo("android.permission.WRITE_EXTERNAL_STORAGE", "Write to external storage"));
        lis.add(new AdhellPermissionInfo("android.permission.USE_SIP", "Use SIP service"));

        // normal
        lis.add(new AdhellPermissionInfo("android.permission.ACCESS_LOCATION_EXTRA_COMMANDS", "Access extra location provider commands"));
        lis.add(new AdhellPermissionInfo("android.permission.GET_PACKAGE_SIZE", "Find out the space used by any package"));
        lis.add(new AdhellPermissionInfo("android.permission.READ_PHONE_STATE", "Allows read access to phone state (phone number, current network info)"));
        lis.add(new AdhellPermissionInfo("android.permission.CHANGE_NETWORK_STATE", "Change network connectivity state"));
        lis.add(new AdhellPermissionInfo("android.permission.CHANGE_WIFI_MULTICAST_STATE", "Enter Wi-Fi Multicast mode"));
        lis.add(new AdhellPermissionInfo("android.permission.CHANGE_WIFI_STATE", "Change Wi-Fi connectivity state"));
        lis.add(new AdhellPermissionInfo("android.permission.DISABLE_KEYGUARD", "Disable the keyguard if it is not secure"));
        lis.add(new AdhellPermissionInfo("android.permission.EXPAND_STATUS_BAR", "Expand or collapse the status bar"));
//        lis.add(new AdhellPermissionInfo("com.android.launcher.permission.INSTALL_SHORTCUT", "Install a shortcut in Launcher"));
        lis.add(new AdhellPermissionInfo("android.permission.MODIFY_AUDIO_SETTINGS", "Modify global audio settings"));
        lis.add(new AdhellPermissionInfo("android.permission.NFC", "Perform I/O operations over NFC"));
        lis.add(new AdhellPermissionInfo("android.permission.READ_SMS", "Read SMS messages"));
//        lis.add(new AdhellPermissionInfo("android.permission.READ_SYNC_SETTINGS", "Read the sync settings"));
//        lis.add(new AdhellPermissionInfo("android.permission.READ_SYNC_STATS", "Read the sync stats"));
//        lis.add(new AdhellPermissionInfo("android.permission.WRITE_SYNC_SETTINGS", "Write the sync settings"));
        lis.add(new AdhellPermissionInfo("android.permission.RECEIVE_BOOT_COMPLETED", "Receive Boot Completed"));
//        lis.add(new AdhellPermissionInfo("com.android.alarm.permission.SET_ALARM", "Set an alarm for the user"));


        return lis;
    }
}
