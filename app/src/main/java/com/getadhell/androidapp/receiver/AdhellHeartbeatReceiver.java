package com.getadhell.androidapp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.getadhell.androidapp.service.HeartbeatIntentService;


public class AdhellHeartbeatReceiver extends BroadcastReceiver {
    private static final String TAG = AdhellHeartbeatReceiver.class.getCanonicalName();

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "Starting service");
        Intent i = new Intent(context, HeartbeatIntentService.class);
        i.putExtra("launchedFrom", "alarm-receiver");
        context.startService(i);
    }
}
