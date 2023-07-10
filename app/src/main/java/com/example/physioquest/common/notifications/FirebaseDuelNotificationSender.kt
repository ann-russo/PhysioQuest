package com.example.physioquest.common.notifications

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.core.app.NotificationCompat
import com.example.physioquest.PhysioQuestActivity
import com.example.physioquest.R

class FirebaseDuelNotificationSender(private val context: Context) : DuelNotificationSender {
    override fun sendDuelEndNotification(duelId: String, title: String, body: String) {
        val channelId = "duel_finished_channel"
        val pendingIntent = createPendingIntent()
        val notificationBuilder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.physioquest_logo_3) // replace with your own icon
            .setContentTitle(title)
            .setContentText(body)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        notificationManager.notify(0, notificationBuilder.build())
    }

    @OptIn(ExperimentalMaterial3Api::class)
    private fun createPendingIntent(): PendingIntent {
        val intent = Intent(context, PhysioQuestActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntentId = 0
        return PendingIntent.getActivity(
            context,
            pendingIntentId,
            intent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
    }
}
