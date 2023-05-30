package com.example.physioquest.service

import com.example.physioquest.model.Answer
import com.example.physioquest.model.Question
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageServiceImpl
@Inject
constructor(private val firestore: FirebaseFirestore) : StorageService {

    override val questions: Flow<List<Question>> = flow {
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
        emit(questions)
    }

    override suspend fun getQuestions(questionId: String): Question? {
        TODO()
    }

    override suspend fun getQuestionsForCategory(category: String): List<Question> {
        return questions.firstOrNull()?.filter { it.category == category } ?: emptyList()
    }

    override suspend fun addQuestions(question: Question) {
        firestore.collection(QUESTIONS_COLLECTION).add(question).await()
    }

    companion object {
        private const val QUESTIONS_COLLECTION = "questions"
        private const val ANSWERS_ARRAY = "answers"
    }
}

