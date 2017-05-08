package com.getadhell.androidapp.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.receiver.BlockedDomainAlarmReceiver;


public class BlockedDomainAlarmHelper {
    private static final String TAG = BlockedDomainAlarmHelper.class.getCanonicalName();

    public static void scheduleBlockedDomainAlarm() {
        Intent intent = new Intent(App.get().getApplicationContext(), BlockedDomainAlarmReceiver.class);
        Log.d(TAG, "Alarm not set. Setting alarm");
        final PendingIntent pIntent = PendingIntent.getBroadcast(App.get().getApplicationContext(), BlockedDomainAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) App.get().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                AlarmManager.INTERVAL_HOUR, pIntent);
    }


    public static void cancelBlockedDomainAlarm() {
        Intent intent = new Intent(App.get().getApplicationContext(), BlockedDomainAlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(App.get().getApplicationContext(), BlockedDomainAlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) App.get().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }

    public static boolean isEnabled() {
        Intent intent = new Intent(App.get().getApplicationContext(), BlockedDomainAlarmReceiver.class);
        boolean alarmUp = (PendingIntent.getBroadcast(App.get().getApplicationContext(), BlockedDomainAlarmReceiver.REQUEST_CODE,
                intent,
                PendingIntent.FLAG_NO_CREATE) != null);
        return alarmUp;
    }
}
