package com.alex.tur.fcm

import android.support.v4.app.NotificationCompat
import android.support.v4.app.NotificationManagerCompat
import com.alex.tur.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import timber.log.Timber
import android.app.NotificationManager
import android.app.NotificationChannel
import android.os.Build

class MyFirebaseMessagingService: FirebaseMessagingService() {

    private val TAG = "myfcm"

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        handleNotification(remoteMessage)

        Timber.tag(TAG).d( "From: %s", remoteMessage.from)
        Timber.tag(TAG).d("Message data: %s", remoteMessage.data)
        Timber.tag(TAG).d("Message messageId: %s", remoteMessage.messageId)
        Timber.tag(TAG).d("Message messageType: %s", remoteMessage.messageType)
        Timber.tag(TAG).d("Message sentTime: %s", remoteMessage.sentTime)
        Timber.tag(TAG).d("Message to: %s", remoteMessage.to)
        Timber.tag(TAG).d("Message collapseKey: %s", remoteMessage)

        // Check if message contains a notification payload.
        Timber.tag(TAG).d("Message Notification title: %s" , remoteMessage.notification?.title)
        Timber.tag(TAG).d("Message Notification body: %s" , remoteMessage.notification?.body)
        Timber.tag(TAG).d("Message Notification clickAction: %s" , remoteMessage.notification?.clickAction)
        Timber.tag(TAG).d("Message Notification color: %s" , remoteMessage.notification?.color)
        Timber.tag(TAG).d("Message Notification icon: %s" , remoteMessage.notification?.icon)
        Timber.tag(TAG).d("Message Notification link: %s" , remoteMessage.notification?.link)
        Timber.tag(TAG).d("Message Notification sound: %s" , remoteMessage.notification?.sound)
        Timber.tag(TAG).d("Message Notification tag: %s" , remoteMessage.notification?.tag)
        Timber.tag(TAG).d("Message Notification titleLocalizationKey: %s" , remoteMessage.notification?.titleLocalizationKey)
        Timber.tag(TAG).d("Message Notification bodyLocalizationArgs: %s" , remoteMessage.notification?.bodyLocalizationArgs)
        Timber.tag(TAG).d("Message Notification bodyLocalizationKey: %s" , remoteMessage.notification?.bodyLocalizationKey)
        Timber.tag(TAG).d("Message Notification titleLocalizationArgs: %s" , remoteMessage.notification?.titleLocalizationArgs)
    }

    private fun handleNotification(remoteMessage: RemoteMessage) {
        val builder = NotificationCompat.Builder(this, CHANNEL_ID_MAIN)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle(remoteMessage.notification?.title)
                .setContentText(remoteMessage.notification?.body)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        val notificationManager = NotificationManagerCompat.from(this)
        notificationManager.notify(ID_NOTIFICATION_DEFAULT, builder.build())
    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_name_main)
            val description = getString(R.string.channel_description_main)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID_MAIN, name, importance)
            channel.description = description
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val CHANNEL_ID_MAIN = "MAIN"

        private const val ID_NOTIFICATION_DEFAULT = 0
    }
}