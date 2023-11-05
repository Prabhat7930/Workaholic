package com.example.workaholic.fcm

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.workaholic.R
import com.example.workaholic.activity.MainActivity
import com.example.workaholic.activity.ProfileActivity
import com.example.workaholic.activity.SignInActivity
import com.example.workaholic.firebase.FireStoreClass
import com.example.workaholic.utils.Constants
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(remoteMmessage: RemoteMessage) {
        super.onMessageReceived(remoteMmessage)

        Log.d("TAG", "From : ${remoteMmessage.from}")

        remoteMmessage.data.isNotEmpty().let {
            Log.d("TAG", "Message data Payload : ${remoteMmessage.data}")

            val title = remoteMmessage.data[Constants.FCM_KEY_TITLE]!!
            val message = remoteMmessage.data[Constants.FCM_KEY_MESSAGE]!!
            Log.e("haha", "haha6")
            getNotification(title, message)
        }

        remoteMmessage.notification?.let {
            Log.d("TAG", "Message Notification Body : ${it.body}")
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        Log.e("TAG", "Refreshed token : $token")
        sendRegistrationToServer(token)
    }

    private fun sendRegistrationToServer(token : String?) {
        val sharedPreferences =
            this.getSharedPreferences(Constants.WORKAHOLIC_PREFERENCES, Context.MODE_PRIVATE)
        val editor: SharedPreferences.Editor = sharedPreferences.edit()
        editor.putString(Constants.FCM_TOKEN, token)
        editor.apply()
    }

    private fun getNotification(title : String, message : String) {

        val intent = if (FireStoreClass().getCurrUserId().isNotEmpty()) {
            Intent(this, MainActivity::class.java)
        }
        else {
            Intent(this, SignInActivity::class.java)
        }
        intent.addFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK or
                Intent.FLAG_ACTIVITY_CLEAR_TASK)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val channelId = this.resources.getString(R.string.default_notification_channel_id)
        val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        Log.e("haha", "haha7")

        val notificationBuilder = NotificationCompat.Builder(
            this, channelId)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setSound(defaultSoundUri)
            .setContentIntent(pendingIntent)

        val notificationManager = getSystemService(
            Context.NOTIFICATION_SERVICE
        )   as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId,
                "Channel Workaholic Title",
                NotificationManager.IMPORTANCE_DEFAULT)

            notificationManager.createNotificationChannel(channel)
        }
        notificationManager.notify(0, notificationBuilder.build())
    }
}