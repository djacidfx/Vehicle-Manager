package com.ninetynineapps.vehiclemanager.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.ninetynineapps.vehiclemanager.activities.MainActivity


class OnBootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        print("On Restart Mobile")
        MainActivity().setAlarmManager()
    }
}