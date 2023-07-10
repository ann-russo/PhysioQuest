package com.example.physioquest.service

import android.util.Log
import com.example.physioquest.model.User
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

class LevelServiceImpl @Inject
constructor() : LevelService {

    override suspend fun awardXp(user: User, xp: Int) {
        user.xp += xp
        while (user.xp >= user.level * 100) {
            user.xp -= user.level * 100
            user.level++
            if (user.level > 20) {
                user.level = 20
                user.xp = 0
                break
            }
        }
        updateXpAndLevel(user)
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
}