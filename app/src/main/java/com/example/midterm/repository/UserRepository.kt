package com.example.midterm.repository

import com.example.midterm.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class UserRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val collection = firestore.collection("users")

    fun getUsers(): Flow<List<User>> = callbackFlow {
        val subscription = collection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val users = snapshot.documents.mapNotNull { doc ->
                    val id = doc.getString("id") ?: doc.id
                    val email = doc.getString("email") ?: ""
                    val role = doc.getString("role") ?: "User"
                    User(id = id, email = email, role = role)
                }
                trySend(users)
            }
        }
        awaitClose { subscription.remove() }
    }

    suspend fun deleteUser(userId: String): Result<Unit> {
        return try {
            collection.document(userId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}