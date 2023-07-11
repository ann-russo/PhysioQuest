package com.example.physioquest.service

import com.example.physioquest.model.User

interface LevelService {
    suspend fun awardXp(user: User, xp: Int)
    suspend fun removeXp(user: User, xp: Int)
    suspend fun updateXpAndLevel(user: User)
    fun getRankName(level: Int): Int
}