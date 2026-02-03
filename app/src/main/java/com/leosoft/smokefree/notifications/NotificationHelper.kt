package com.leosoft.smokefree.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {
    const val CHANNEL_ID = "smokefree_channel"
    const val EXTRA_MESSAGE_ID = "extra_message_id"

    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Telkin Bildirimleri",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Sigara bırakma telkin mesajları"
        }
        val manager = context.getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)
    }

    fun showNotification(
        context: Context,
        messageId: Int,
        preview: String,
        fullText: String,
        pendingIntent: android.app.PendingIntent
    ) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.sym_def_app_icon)
            .setContentTitle("Sigarasız Koç")
            .setContentText(preview)
            .setStyle(NotificationCompat.BigTextStyle().bigText(fullText))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(messageId, notification)
    }
}
