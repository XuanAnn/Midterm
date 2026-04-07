package com.example.midterm.repository

import android.net.Uri
import com.example.midterm.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.*

class ProductRepository {
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val productsCollection = firestore.collection("products")

    fun getProducts(): Flow<List<Product>> = callbackFlow {
        val subscription = productsCollection.addSnapshotListener { snapshot, error ->
            if (error != null) {
                close(error)
                return@addSnapshotListener
            }
            if (snapshot != null) {
                val products = snapshot.documents.mapNotNull { doc ->
                    doc.toObject(Product::class.java)?.copy(id = doc.id)
                }
                trySend(products)
            }
        }
        awaitClose { subscription.remove() }
    }

    suspend fun addProduct(product: Product, imageUri: Uri?): Result<Unit> {
        return try {
            var imageUrl = product.imageUrl
            if (imageUri != null) {
                imageUrl = uploadImage(imageUri)
            }
            val finalProduct = product.copy(imageUrl = imageUrl)
            productsCollection.add(finalProduct.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(product: Product, imageUri: Uri?): Result<Unit> {
        return try {
            var imageUrl = product.imageUrl
            if (imageUri != null) {
                imageUrl = uploadImage(imageUri)
            }
            val finalProduct = product.copy(imageUrl = imageUrl)
            productsCollection.document(product.id).update(finalProduct.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteProduct(productId: String): Result<Unit> {
        return try {
            productsCollection.document(productId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun uploadImage(uri: Uri): String {
        val fileName = UUID.randomUUID().toString()
        val ref = storage.reference.child("images/$fileName")
        ref.putFile(uri).await()
        return ref.downloadUrl.await().toString()
    }
}
