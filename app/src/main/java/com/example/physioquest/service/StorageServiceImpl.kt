package com.example.physioquest.service

import android.util.Log
import com.example.physioquest.R
import com.example.physioquest.common.snackbar.SnackbarManager
import com.example.physioquest.model.Answer
import com.example.physioquest.model.Duel
import com.example.physioquest.model.Question
import com.example.physioquest.model.User
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

class StorageServiceImpl
@Inject
constructor(private val firestore: FirebaseFirestore) : StorageService {

    override suspend fun getQuestions(): List<Question> {
        val snapshot = firestore.collection(QUESTIONS_COLLECTION)
            .orderBy("category")
            .get()
            .await()

        return mapSnapshotToQuestions(snapshot)
    }

    private fun mapSnapshotToQuestions(snapshot: QuerySnapshot): List<Question> {
        return snapshot.documents.mapNotNull { document ->
            val rawAnswers = document[ANSWERS_ARRAY]
            val answers = (rawAnswers as? List<*>)?.filterIsInstance<Map<*, *>>()

            answers?.mapNotNull { answersMap ->
                val content = answersMap["content"]
                val isCorrect = answersMap["isCorrect"]

                if (content is String && isCorrect is Boolean) {
                    Answer(content = content, isCorrect = isCorrect)
                } else {
                    null
                }
            }?.let { answerList ->
                Question(
                    id = document.id,
                    category = document["category"] as? String ?: "",
                    content = document["content"] as? String ?: "",
                    answers = answerList
                )
            }
        }
    }

    override suspend fun getQuestionsForCategory(category: String): List<Question> {
        val questions = getQuestions()
        return questions.filter { it.category == category }
    }

    override suspend fun getRandomQuestions(): List<Question> {
        val randomQuestions = mutableListOf<Question>()

        val questions = getQuestions()
        val questionsSize = questions.size
        val randomIndices = mutableSetOf<Int>()

        while (randomIndices.size < 15) {
            val randomIndex = (0 until questionsSize).random()
            randomIndices.add(randomIndex)
        }

        for (index in randomIndices) {
            randomQuestions.add(questions[index])
        }

        return randomQuestions
    }


    override suspend fun getUsersFromDatabase(): List<User> =
        suspendCancellableCoroutine { continuation ->
            firestore.collection(USERS_COLLECTION)
                .get()
                .addOnSuccessListener { querySnapshot ->
                    val users = mutableListOf<User>()
                    for (document in querySnapshot.documents) {
                        val user = document.toObject(User::class.java)
                        if (user != null) {
                            users.add(user)
                        }
                    }
                    continuation.resumeWith(Result.success(users))
                }
                .addOnFailureListener { exception ->
                    continuation.resumeWithException(exception)
                }
        }

    override suspend fun getHighestXpUsers(): List<User> = suspendCoroutine { continuation ->
        firestore.collection(USERS_COLLECTION)
            .orderBy("xp", Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                val users = documents.mapNotNull { document ->
                    document.toObject(User::class.java)
                }
                Log.d(TAG, "top users size: ${users.size}")
                continuation.resume(users)
            }
            .addOnFailureListener { exception ->
                continuation.resumeWithException(exception)
            }
    }

    override suspend fun getRandomUserFromDatabase(currentUserId: String): User {
        var randomOpponent = User()
        val availableUsers = getUsersFromDatabase().filter { it.id != currentUserId }
        if (availableUsers.isNotEmpty()) {
            val randomIndex = Random.nextInt(0, availableUsers.size)
            randomOpponent = availableUsers[randomIndex]
        }
        return randomOpponent
    }

    override fun getDuel(duelId: String): Flow<Duel> = callbackFlow {
        val duelRef = firestore.collection(DUELS_COLLECTION).document(duelId)
        val listenerRegistration = duelRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.d(TAG, error.toString())
                close(error)
                return@addSnapshotListener
            }

            if (snapshot != null && snapshot.exists()) {
                val duel = snapshot.toObject(Duel::class.java)
                if (duel != null) {
                    trySend(duel)
                }
            }
        }
        awaitClose { listenerRegistration.remove() }
    }

    override suspend fun saveDuel(duel: Duel) {
        try {
            val documentReference = firestore.collection(DUELS_COLLECTION).document()
            duel.id = documentReference.id
            documentReference.set(duel).await()
            Log.d(TAG, "Duel ${duel.id} saved successfully")
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
            SnackbarManager.showMessage(R.string.duel_start_error)
        }
    }

    override suspend fun updateDuel(duel: Duel) {
        val duelRef = firestore.collection(DUELS_COLLECTION).document(duel.id)
        duelRef.set(duel).await()
        Log.d(TAG, "Duel ${duel.id} updated successfully")
    }

    override suspend fun deleteDuel(duelId: String) {
        val duelRef = firestore.collection(DUELS_COLLECTION).document(duelId)
        duelRef.delete().await()
        Log.d(TAG, "Duel $duelId deleted")
    }

    override suspend fun getUnfinishedDuels(): List<Duel> {
        val query = firestore.collection(DUELS_COLLECTION)
            .whereEqualTo("initUserFinished", false)
            .whereEqualTo("opponentUserFinished", false)
        return try {
            val snapshot = query.get().await()
            snapshot.toObjects(Duel::class.java)
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
            emptyList()
        }
    }

    private suspend fun getQuestionsForDuel(questionList: List<Question>): List<Question> {
        val questionIds = questionList.map { it.id }
        val querySnapshot = firestore.collection(QUESTIONS_COLLECTION)
            .whereIn(FieldPath.documentId(), questionIds)
            .get()
            .await()
        return mapSnapshotToQuestions(querySnapshot)
    }

    override suspend fun findUnfinishedDuelByOpponentUserId(userId: String): Duel? {
        val unfinishedDuels = getUnfinishedDuels() // get all unfinished duels
        for (duel in unfinishedDuels) {
            if (duel.opponentUser.id == userId) {
                Log.d(TAG, "Found unfinished duel ${duel.id}")
                val mappedQuestions = getQuestionsForDuel(duel.randomQuestionsList)
                return Duel(
                    id = duel.id,
                    initUser = duel.initUser,
                    opponentUser = duel.opponentUser,
                    initUserFinished = duel.initUserFinished,
                    opponentUserFinished = duel.opponentUserFinished,
                    startTimestamp = duel.startTimestamp,
                    finishTimestamp = duel.finishTimestamp,
                    randomQuestionsList = mappedQuestions,
                    winnerUser = duel.winnerUser
                )
            }
        }
        return null
    }

    override suspend fun findUnfinishedDuelByUserId(userId: String): Duel? {
        val duelsAsInitiator = getUnfinishedDuelsByInitiator(userId)
        val duelsAsOpponent = getUnfinishedDuelsByOpponent(userId)

        // Return the first duel found or null if no duel was found
        return duelsAsInitiator.firstOrNull() ?: duelsAsOpponent.firstOrNull()
    }

    private suspend fun getUnfinishedDuelsByInitiator(userId: String): List<Duel> {
        // Current user is the initiator and finished the duel, but the opponent didn't play yet

        val query = firestore.collection(DUELS_COLLECTION)
            .whereEqualTo("initUser.id", userId)
            .whereEqualTo("initUserFinished", true)
            .whereEqualTo("opponentUserFinished", false)
        return try {
            val snapshot = query.get().await()
            snapshot.toObjects(Duel::class.java)
        } catch (e: Exception) {
            Log.d(TAG, e.toString())
            emptyList()
        }
    }

    private suspend fun getUnfinishedDuelsByOpponent(userId: String): List<Duel> {
        // Current user was selected as opponent in duel and didn't play yet

        val query = firestore.collection(DUELS_COLLECTION)
            .whereEqualTo("opponentUser.id", userId)
            .whereEqualTo("opponentUserFinished", false)
        return try {
            val snapshot = query.get().await()
            snapshot.toObjects(Duel::class.java)
        } catch (e: Exception) {
            Log.d("getUnfinishedDuelsByOpponent", e.toString())
            emptyList()
        }
    }

    override fun listenForDuelFinish(duelId: String): Flow<Duel> = callbackFlow {
        val duelDocRef = firestore.collection(DUELS_COLLECTION).document(duelId)
        val listenerRegistration = duelDocRef.addSnapshotListener { snapshot, error ->
            if (error != null) {
                Log.d(TAG, "Listen failed", error)
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null && snapshot.exists()) {
                val updatedDuel = snapshot.toObject(Duel::class.java)
                if (updatedDuel != null) {
                    Log.d(TAG, "Updated duel id ${updatedDuel.id}")
                    trySend(updatedDuel)
                }
            }
        }
        awaitClose { listenerRegistration.remove() }
    }

    companion object {
        private const val TAG = "StorageService"
        private const val QUESTIONS_COLLECTION = "questions"
        private const val ANSWERS_ARRAY = "answers"
        private const val USERS_COLLECTION = "users"
        private const val DUELS_COLLECTION = "duels"
    }
}