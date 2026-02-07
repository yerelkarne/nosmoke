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
    const val EXTRA_ROUTE = "extra_route"

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

    fun showSystemNotification(context: Context, title: String, message: String, route: String) {
        val intent = android.content.Intent(context, com.leosoft.smokefree.MainActivity::class.java).apply {
            flags = android.content.Intent.FLAG_ACTIVITY_NEW_TASK or android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_ROUTE, route)
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            route.hashCode(),
            intent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.sym_def_app_icon)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(route.hashCode(), notification)
    }
}
