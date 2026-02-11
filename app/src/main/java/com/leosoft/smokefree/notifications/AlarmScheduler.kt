package com.leosoft.smokefree.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.leosoft.smokefree.data.MessageRepository
import com.leosoft.smokefree.data.SettingsDataStore
import com.leosoft.smokefree.receiver.NotificationReceiver
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.flow.first
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

class AlarmScheduler(private val context: Context) {
    private val alarmManager = context.getSystemService(AlarmManager::class.java)
    private val settingsStore = SettingsDataStore(context)

    fun scheduleToday(count: Int, startMinutes: Int, endMinutes: Int) {
        // Ayar değiştiğinde aynı gün için eski alarmları temizle.
        cancelTodayAlarms()
        if (endMinutes <= startMinutes) return

        val times = calculateTimes(count, startMinutes, endMinutes)
        val today = LocalDate.now()
        val messages = MessageRepository.loadMessages(context)
        val zone = ZoneId.systemDefault()

        times.forEachIndexed { index, minutes ->
            // Her mesaj için bugünün kesin alarm zamanını hesapla.
            val dateTime = LocalDateTime.of(today.year, today.month, today.dayOfMonth, minutes / 60, minutes % 60)
            val triggerAt = dateTime.atZone(zone).toInstant().toEpochMilli()
            val message = messages[index % messages.size]
            val requestCode = buildRequestCode(today, index)
            val pendingIntent = buildPendingIntent(requestCode, message.id)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Dakik bildirim için kesin alarm.
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
            }
        }

        runBlocking {
            // Yeniden planlama için son planlanan günü kaydet.
            settingsStore.updateLastScheduledDay(today.toEpochDay())
        }
    }

    fun rescheduleFromStore() {
        runBlocking {
            // Sistem olaylarında kaydedilen ayarları tekrar uygula.
            val settings = settingsStore.settingsFlow.first()
            scheduleToday(settings.dailyCount, settings.startMinutes, settings.endMinutes)
        }
    }

    fun cancelTodayAlarms() {
        val today = LocalDate.now()
        // Olası tüm alarm requestCode'larını iptal et.
        for (index in 0 until 12) {
            val requestCode = buildRequestCode(today, index)
            val pendingIntent = buildPendingIntent(requestCode, 0)
            alarmManager.cancel(pendingIntent)
        }
    }

    private fun buildRequestCode(date: LocalDate, index: Int): Int {
        return (date.toEpochDay() * 100 + index).toInt()
    }

    private fun buildPendingIntent(requestCode: Int, messageId: Int): PendingIntent {
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            putExtra(NotificationHelper.EXTRA_MESSAGE_ID, messageId)
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    companion object {
        fun calculateTimes(count: Int, startMinutes: Int, endMinutes: Int): List<Int> {
            if (count <= 1) {
                return listOf((startMinutes + endMinutes) / 2)
            }
            val interval = (endMinutes - startMinutes).toDouble() / (count - 1)
            return List(count) { index ->
                (startMinutes + index * interval).toInt()
            }
        }
    }
}
