package com.getadhell.androidapp.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.getadhell.androidapp.service.BlockedDomainService;

public class BlockedDomainAlarmReceiver extends BroadcastReceiver {
    public static final String TAG = BlockedDomainAlarmReceiver.class.getCanonicalName();
    public static final int REQUEST_CODE = 502742;

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Received from alarmManager");
        Intent i = new Intent(context, BlockedDomainService.class);
        i.putExtra("launchedFrom", "alarm-receiver");
        context.startService(i);
    }
}
