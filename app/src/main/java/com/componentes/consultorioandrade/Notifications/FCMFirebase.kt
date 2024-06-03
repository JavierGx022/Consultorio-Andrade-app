package com.componentes.consultorioandrade.Notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.componentes.consultorioandrade.R
import com.componentes.consultorioandrade.View.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlin.random.Random

class FCMFirebase: FirebaseMessagingService() {
    private val random= Random

    override fun onMessageReceived(message: RemoteMessage) {
        message.notification?.let {m->
            sendNotification(m)
        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    private fun sendNotification(message: RemoteMessage.Notification){
    val intent = Intent(this, MainActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
    }
        val pending= PendingIntent.getActivity(this,0, intent, PendingIntent.FLAG_IMMUTABLE)

        val channelId= this.getString(R.string.default_notification)
        val notifcationBuilder= NotificationCompat.Builder(this, channelId)
            .setContentTitle(message.title)
            .setContentText(message.body)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setAutoCancel(true)
            .setContentIntent(pending)

        val manager= getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel= NotificationChannel(channelId, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)
            manager.createNotificationChannel(channel)
        }
        manager.notify(random.nextInt(), notifcationBuilder.build())

    }

    companion object{
        const val CHANNEL_NAME= "FCM notificacion"
    }
}