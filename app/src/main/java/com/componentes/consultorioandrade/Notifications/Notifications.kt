package com.componentes.consultorioandrade.Notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.componentes.consultorioandrade.View.MainActivity
import java.util.Calendar

class Notifications {
    fun scheduleNotification(context: Context, notificationTime: Calendar, requestCode: Int) {
        val intent = Intent(context, MainActivity::class.java).apply {
            putExtra("NOTIFICATION_ID", requestCode)
        }
        val pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, notificationTime.timeInMillis, pendingIntent)
    }
}