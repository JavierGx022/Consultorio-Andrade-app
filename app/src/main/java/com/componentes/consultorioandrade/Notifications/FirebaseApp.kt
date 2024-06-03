package com.componentes.consultorioandrade.Notifications

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class FirebaseApp: Application() {
    companion object{
        const val FMC_CHANNEL_ID= "FMC_CHANNEL_ID"
    }

    override fun onCreate() {
        super.onCreate()
        if(android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O){
            val channel= NotificationChannel(FMC_CHANNEL_ID, "Fmc_channel", NotificationManager.IMPORTANCE_HIGH)
            val manager= getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }
}