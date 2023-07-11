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
        user.level = calculateLevel(user.xp)
        if (user.level > 20) {
            user.level = 20
        }
        updateXpAndLevel(user)
    }

    override suspend fun removeXp(user: User, xp: Int) {
        user.xp -= xp
        if (user.xp < 0) {
            user.xp = 0
        }
        user.level = calculateLevel(user.xp)
        if (user.level > 20) {
            user.level = 20
        }
        updateXpAndLevel(user)
    }

    private fun calculateLevel(xp: Int): Int {
        var level = 1
        while (level * 100 <= xp) {
            level++
        }
        // If the XP is exactly a multiple of 100, the level will be one too high. So, we subtract one.
        level--
        // Ensure the level never goes below 1.
        if (level < 1) {
            level = 1
        }
        return level
    }

    override suspend fun updateXpAndLevel(user: User) {
        val docRef = FirebaseFirestore.getInstance().collection("users").document(user.id)
        docRef
            .update(
                "xp", user.xp,
                "level", user.level
            )
            .addOnSuccessListener {
                Log.d("LevelService", "User Xp and Level updated")
            }
            .addOnFailureListener { exception ->
                Log.w("LevelService", "Error updating user XP and Level", exception)
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
}