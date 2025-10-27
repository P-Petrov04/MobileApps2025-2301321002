package com.example.runningtrackerapplication

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.Query

class RunRepository {
    private val db = FirebaseFirestore.getInstance()
    private val runsCollection = db.collection("runs")

    suspend fun addRun(run: Run) {
        try {
            runsCollection.add(run).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getRuns() = runsCollection.orderBy("timestamp", Query.Direction.DESCENDING)

    suspend fun updateRun(run: Run) {
        try {
            runsCollection.document(run.id).set(run, SetOptions.merge()).await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun deleteRun(runId: String) {
        try {
            runsCollection.document(runId).delete().await()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}