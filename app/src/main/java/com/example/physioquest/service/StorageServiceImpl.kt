package com.example.physioquest.service

import com.example.physioquest.model.Antwort
import com.example.physioquest.model.Frage
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class StorageServiceImpl
@Inject
constructor(private val firestore: FirebaseFirestore) : StorageService {

    override val fragen: Flow<List<Frage>> = flow {
        val snapshot = firestore.collection(FRAGEN_COLLECTION)
            .orderBy("frageInhalt")
            .get()
            .await()
        val fragen = snapshot.documents.mapNotNull { document ->
            val antwortenSnapshot = firestore.collection(FRAGEN_COLLECTION)
                .document(document.id)
                .collection(ANTWORTEN_COLLECTION)
                .get()
                .await()
            val antworten = antwortenSnapshot.documents.mapNotNull { antwortDoc ->
                antwortDoc.toObject(Antwort::class.java)?.copy(id = antwortDoc.id)
            }
            document.toObject(Frage::class.java)?.copy(id = document.id, antworten = antworten)
        }
        emit(fragen)
    }

    override suspend fun getFrage(frageId: String): Frage? {
        TODO()
    }

    companion object {
        private const val FRAGEN_COLLECTION = "fragen"
        private const val ANTWORTEN_COLLECTION = "antworten"
    }
}

