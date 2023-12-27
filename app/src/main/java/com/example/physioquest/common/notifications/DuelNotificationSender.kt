package com.example.physioquest.common.notifications

fun interface DuelNotificationSender {
    fun sendDuelEndNotification(duelId: String, title: String, body: String)
}