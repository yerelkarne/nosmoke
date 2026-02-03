package com.leosoft.smokefree.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.leosoft.smokefree.notifications.AlarmScheduler

class RescheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Sistem olaylarında günlük planı yeniden kur.
        AlarmScheduler(context).rescheduleFromStore()
    }
}
