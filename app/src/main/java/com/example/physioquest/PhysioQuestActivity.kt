package com.example.physioquest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import com.example.physioquest.service.AccountService
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
@ExperimentalMaterial3Api
class PhysioQuestActivity : ComponentActivity() {
    @Inject
    lateinit var accountService: AccountService
    private lateinit var duelIdFromNotification: MutableState<String?>

    @OptIn(ExperimentalAnimationApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel()
        setupFirebaseTokenRefreshListener()

        setContent {
            val navController = rememberAnimatedNavController()
            val notificationType = intent.extras?.getString("type")
            duelIdFromNotification = remember { mutableStateOf(if (notificationType == "duel_finished") intent.extras?.getString("duelId") else null) }
            PhysioQuestApp(navController, duelIdFromNotification)
        }
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        setIntent(intent)
        val notificationType = intent?.extras?.getString("type")
        duelIdFromNotification.value = if (notificationType == "duel_finished") intent?.extras?.getString("duelId") else null
    }

    private fun createNotificationChannel() {
        val name = getString(R.string.channel_name)
        val descriptionText = getString(R.string.channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("duel_finished_channel", name, importance).apply {
            description = descriptionText
        }
        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun setupFirebaseTokenRefreshListener() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            val token = task.result
            accountService.updateToken(token)
        }
    }
}