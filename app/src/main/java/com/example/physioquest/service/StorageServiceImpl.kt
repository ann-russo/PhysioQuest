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
            val antworten = document.get("antworten") as? List<Map<String, Any>>
            if (antworten != null) {
                val antwortenList = antworten.mapNotNull { antwortMap ->
                    Antwort(
                        antwortInhalt = antwortMap["antwortInhalt"] as? String ?: return@mapNotNull null,
                        antwortKorrekt = antwortMap["antwortKorrekt"] as? Boolean ?: return@mapNotNull null,
                    )
                }
                Frage(
                    id = document.id,
                    kategorie = document.get("kategorie") as? String ?: "",
                    frageInhalt = document.get("frageInhalt") as? String ?: "",
                    antworten = antwortenList
                )
            } else {
                null
            }
        }
        emit(fragen)
    }

    override suspend fun getFrage(frageId: String): Frage? {
        TODO()
    }

    override suspend fun addFrage(frage: Frage) {
        firestore.collection(FRAGEN_COLLECTION).add(frage).await()
    }

    companion object {
        private const val FRAGEN_COLLECTION = "fragen"
    }
}

