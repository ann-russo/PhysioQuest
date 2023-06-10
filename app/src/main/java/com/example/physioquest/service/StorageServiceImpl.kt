package com.example.physioquest.service

import com.example.physioquest.model.Answer
import com.example.physioquest.model.Question
import com.example.physioquest.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import kotlin.coroutines.resumeWithException
import kotlin.random.Random

class StorageServiceImpl
@Inject
constructor(private val firestore: FirebaseFirestore) : StorageService {

    override suspend fun getQuestions(): List<Question> {
        val snapshot = firestore.collection(QUESTIONS_COLLECTION)
            .orderBy("category")
            .get()
            .await()

        val questions = snapshot.documents.mapNotNull { document ->
            val answers = document.get(ANSWERS_ARRAY) as? List<Map<String, Any>>
            if (answers != null) {
                val answerList = answers.mapNotNull { answersMap ->
                    Answer(
                        content = answersMap["content"] as? String ?: return@mapNotNull null,
                        isCorrect = answersMap["isCorrect"] as? Boolean ?: return@mapNotNull null,
                    )
                }
                Question(
                    id = document.id,
                    category = document.get("category") as? String ?: "",
                    content = document.get("content") as? String ?: "",
                    answers = answerList
                )
            } else {
                null
            }
        }
        return questions
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

    override suspend fun getRandomUserFromDatabase(currentUserId: String): User {
        var randomOpponent = User()
        val availableUsers = getUsersFromDatabase().filter { it.id != currentUserId }
        if (availableUsers.isNotEmpty()) {
            val randomIndex = Random.nextInt(0, availableUsers.size)
            randomOpponent = availableUsers[randomIndex]
        }
        return randomOpponent
    }

    companion object {
        private const val QUESTIONS_COLLECTION = "questions"
        private const val ANSWERS_ARRAY = "answers"
        private const val USERS_COLLECTION = "users"
    }
}

