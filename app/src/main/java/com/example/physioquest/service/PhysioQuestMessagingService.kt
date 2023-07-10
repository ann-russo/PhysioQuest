package com.example.physioquest.service

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class PhysioQuestMessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        if (remoteMessage.data.isNotEmpty()) {
            Log.d(TAG, "Message data payload: " + remoteMessage.data)
            val duelId = remoteMessage.data["duelId"]
            if (duelId != null) {
                // Handle the duelId, e.g., by saving it to shared preferences
                Log.d(TAG, "Received duelId: $duelId")
            }
        }
    }

    override fun onNewToken(token: String) {
        Log.d(TAG, "Refreshed token: $token")
    }

    companion object {
        private const val TAG = "PhysioQuestMsgService"
    }
}