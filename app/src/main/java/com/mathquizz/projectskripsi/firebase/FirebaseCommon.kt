package com.mathquizz.projectskripsi.firebase


import android.util.Log
import com.mathquizz.projectskripsi.data.Materi
import com.mathquizz.projectskripsi.util.Constants
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.SetOptions

import kotlinx.coroutines.tasks.await


class FirebaseCommon {

    val firestore = FirebaseFirestore.getInstance()

    suspend fun getMateriList(collectionName: String = "materi"): List<Materi> {
        return try {
            val snapshot = firestore.collection(collectionName)
                .orderBy("urutan")
                .get()
                .await()
            snapshot.toObjects(Materi::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseCommon", "Error fetching materi list from $collectionName", e)
            emptyList()
        }
    }


    suspend fun getMateriById(materiId: String): Materi? {
        return try {
            val document = firestore.collection("materi").document(materiId).get().await()
            document.toObject(Materi::class.java)
        } catch (e: Exception) {
            Log.e("FirebaseCommon", "Error fetching materi by ID", e)
            null
        }
    }

    suspend fun getSubMateriList(collectionName: String = "materi", materiId: String): QuerySnapshot {
        return firestore.collection(collectionName)
            .document(materiId)
            .collection("submateri")
            .orderBy("urutan")
            .get().await()
    }


    suspend fun getQuisQuestion(materiId: String, subMateriId: String): QuerySnapshot {
        return firestore.collection("materi")
            .document(materiId)
            .collection("submateri")
            .document(subMateriId)
            .collection("quis")
            .get()
            .await()
    }

    suspend fun getModul(materiId: String, subMateriId: String): QuerySnapshot {
        return firestore.collection("materi")
            .document(materiId)
            .collection("submateri")
            .document(subMateriId)
            .collection("modul")
            .get()
            .await()
    }

    // Function to get current progress from Firestore for a specific materiId
    suspend fun getCurrentProgress(userId: String, materiId: String): Int {
        val progressDocument = firestore.collection(Constants.USER_COLLECTION)
            .document(userId)
            .collection(Constants.PROGRESS_COLLECTION)
            .document(materiId)
            .get()
            .await()

        return progressDocument.getLong("progressint")?.toInt() ?: 0
    }

    suspend fun updateProgressAndProcess(
        userId: String,
        materiId: String,
        subMateriId: String,
        newProgress: Int
    ): Pair<Boolean, Boolean> {
        val progressCollection = firestore.collection(Constants.USER_COLLECTION)
            .document(userId)
            .collection(Constants.PROGRESS_COLLECTION)
            .document(materiId)

        val processCollection = firestore.collection(Constants.USER_COLLECTION)
            .document(userId)
            .collection(Constants.PROGRESS_COLLECTION)
            .document(materiId)
            .collection(Constants.PROSESS_COLLECTION)
            .document(subMateriId)

        return try {
            firestore.runTransaction { transaction ->
                val progressDocumentSnapshot = transaction.get(progressCollection)
                val processDocumentSnapshot = transaction.get(processCollection)

                val currentProgress = progressDocumentSnapshot.getLong("progressint")?.toInt() ?: 0
                val issuccess = progressDocumentSnapshot.getBoolean("issucsess") ?: false
                val processExists = processDocumentSnapshot.exists()
                val processStatus = processDocumentSnapshot.getBoolean("prosess") ?: false

                if (!issuccess && (!processExists || !processStatus)) {
                    val updatedProgress = (currentProgress + newProgress).coerceAtMost(100)
                    transaction.set(progressCollection, mapOf("progressint" to updatedProgress), SetOptions.merge())
                    transaction.set(processCollection, mapOf("prosess" to true), SetOptions.merge())
                }

                Pair(issuccess, processStatus)
            }.await()
        } catch (e: Exception) {
            Log.e(
                "FirebaseCommon",
                "Error saat memperbarui progress dan status proses untuk materiId: $materiId, subMateriId: $subMateriId",
                e
            )
            Pair(false, false)
        }
    }

    suspend fun updateProgressBasedOnQuiz(
        userId: String,
        materiId: String,
        correctAnswers: Int
    ) {
        val progressDocRef = firestore.collection(Constants.USER_COLLECTION)
            .document(userId)
            .collection(Constants.PROGRESS_COLLECTION)
            .document(materiId) // Use materiId as document ID for progress

        val processCollectionRef = firestore.collection(Constants.USER_COLLECTION)
            .document(userId)
            .collection(Constants.PROGRESS_COLLECTION)
            .document(materiId)
            .collection(Constants.PROSESS_COLLECTION)

        val quizProgress = when (correctAnswers) {
            5 -> 50
            4 -> 40
            3 -> 30
            2 -> 20
            1 -> 10
            0 -> 0
            else -> 0
        }

        val batch = firestore.batch()

        try {
//            // Retrieve existing progress for the module
            if (correctAnswers < 3) {
                // Get all documents in PROSESS_COLLECTION
                val processDocuments = processCollectionRef.get().await()
                for (processDocument in processDocuments) {
                    batch.delete(processCollectionRef.document(processDocument.id))
                }
                // Delete progressint field
                batch.update(progressDocRef, "progressint", FieldValue.delete())
                // Commit the batch
                batch.commit().await()

                Log.d("FirebaseCommon", "Progress field and all process documents deleted for materiId: $materiId due to insufficient correct answers.")

            } else {
                // Retrieve existing progress for the module
                val document = progressDocRef.get().await()
                val existingProgress = document.getLong("progressint")?.toInt() ?: 0

                // Update progress only if it changes
                val newProgress = minOf(existingProgress + quizProgress, 100) // Accumulate but cap at 100

                if (newProgress > existingProgress) {
                    batch.set(progressDocRef, mapOf("progressint" to newProgress), SetOptions.merge())
                    // Commit the batch
                    batch.commit().await()
                    Log.d("FirebaseCommon", "Progress updated to $newProgress successfully for materiId: $materiId")
                }
            }
        } catch (e: Exception) {
            Log.e("FirebaseCommon", "Error updating progress for materiId: $materiId", e)
        }
    }


}



