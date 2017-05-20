package com.getadhell.androidapp.utils;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.getadhell.androidapp.App;
import com.getadhell.androidapp.receiver.AdhellHeartbeatReceiver;

public class HeartbeatAlarmHelper {
    private static final String TAG = HeartbeatAlarmHelper.class.getCanonicalName();
    private static final int UNIQUE_CODE = 29435723;

    public static void scheduleAlarm() {
        Intent intent = new Intent(App.get().getApplicationContext(), AdhellHeartbeatReceiver.class);
        Log.d(TAG, "Setting alarm for heartbeat");
        final PendingIntent pIntent = PendingIntent.getBroadcast(App.get().getApplicationContext(), UNIQUE_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) App.get().getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis,
                AlarmManager.INTERVAL_DAY, pIntent);
    }

}
