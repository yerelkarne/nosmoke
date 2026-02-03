package com.leosoft.smokefree.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.leosoft.smokefree.MainActivity
import com.leosoft.smokefree.data.MessageRepository
import com.leosoft.smokefree.notifications.NotificationHelper

class NotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // Alarm tetiklendiğinde mesajı bulup bildirimi göster.
        val messageId = intent.getIntExtra(NotificationHelper.EXTRA_MESSAGE_ID, -1)
        val message = MessageRepository.findMessage(context, messageId)
        val fullText = message?.text ?: "Bugün de sağlığın için harika bir adım atıyorsun."
        val preview = if (fullText.length > 60) {
            fullText.take(60) + "…"
        } else {
            fullText
        }

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(NotificationHelper.EXTRA_MESSAGE_ID, messageId)
        }
        val pendingIntent = android.app.PendingIntent.getActivity(
            context,
            messageId,
            openIntent,
            android.app.PendingIntent.FLAG_UPDATE_CURRENT or android.app.PendingIntent.FLAG_IMMUTABLE
        )

        NotificationHelper.showNotification(context, messageId, preview, fullText, pendingIntent)
    }
}
