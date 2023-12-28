package com.example.physioquest.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.app.NotificationCompat
import com.example.physioquest.PhysioQuestActivity
import com.example.physioquest.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PhysioQuestMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {
            when (val notificationType = remoteMessage.data["type"]) {
                "opponent_finished" -> handleOpponentFinishedNotification(notificationType)
            }
        }
    }

    private fun handleOpponentFinishedNotification(notificationType: String) {
        // Handle the opponent finished notification
        // Show notification to the user that opponent has finished the duel

        val channelId = "opponent_finished_channel"
        val channelName = "Opponent Finished"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val notificationChannel = NotificationChannel(channelId, channelName, importance)
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)

        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.physioquest_logo_3)
            .setContentTitle("Opponent Finished")
            .setContentText("Your opponent has finished the duel. Tap to play your turn.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(getPendingIntent(notificationType))
            .setAutoCancel(true)
            .build()

        notificationManager.notify(0, notification)
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun getPendingIntent(notificationType: String): PendingIntent {
        val intent = Intent(this, PhysioQuestActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("type", notificationType)
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }


    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token successfully.")
    }

    companion object {
        private const val TAG = "PhysioQuestMsgService"
    }
}