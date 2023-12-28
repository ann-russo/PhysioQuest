package com.example.physioquest.service

import android.util.Log
import com.example.physioquest.R
import com.example.physioquest.model.User
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class LevelServiceImpl @Inject
constructor() : LevelService {

    override suspend fun awardXp(user: User, xp: Int) {
        user.xp += xp
        user.level = calculateLevel(user.xp).coerceAtMost(20)
        updateXpAndLevel(user)
        Log.d(TAG, "Added $xp XP to user ${user.username}")
    }

    override suspend fun removeXp(user: User, xp: Int) {
        user.xp = (user.xp - xp).coerceAtLeast(0)
        user.level = calculateLevel(user.xp).coerceAtMost(20)
        updateXpAndLevel(user)
    }

    override fun calculateLevel(xp: Int): Int {
        var level = 1
        while ((100 * (level + 1) * (level + 1) / 2) <= xp) {
            level++
        }
        // Ensure the level never goes below 1 or above 20.
        if (level < 1) {
            level = 1
        } else if (level > 20) {
            level = 20
        }
        return level
    }

    override fun calculateXpInCurrentLevel(xp: Int, level: Int): Int {
        // The total XP needed to reach the current level.
        val totalXpForCurrentLevel = if (level > 1) 100 * (level) * (level) / 2 else 0
        // The XP points accumulated on the current level are the total XP minus the XP needed to reach the current level.
        return xp - totalXpForCurrentLevel
    }

    override fun calculateXpForNextLevel(level: Int): Int {
        // The XP needed for next level is 100 times the next level number.
        return 100 * (level + 1)
    }

    override suspend fun updateXpAndLevel(user: User) {
        val docRef = FirebaseFirestore.getInstance().collection("users").document(user.id)
        docRef
            .update(
                "xp", user.xp,
                "level", user.level
            )
            .addOnSuccessListener {
                Log.d(TAG, "User XP and Level updated")
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error updating XP and Level", exception)
            }
    }

    override fun getRankName(level: Int): Int {
        return when {
            level in 1..4 -> R.string.rank_beginner
            level in 5..9 -> R.string.rank_intermediate
            level in 10..14 -> R.string.rank_superior
            level >= 15 -> R.string.rank_extreme
            else -> R.string.rank_default
        }
    }

    companion object {
        private const val TAG = "LevelService"
    }
}