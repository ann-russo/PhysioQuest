package com.example.physioquest.common.notifications

interface DuelNotificationSender {
    fun sendDuelEndNotification(duelId: String, title: String, body: String)
}