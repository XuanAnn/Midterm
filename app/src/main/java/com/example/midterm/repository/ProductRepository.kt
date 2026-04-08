package com.example.midterm.repository

import android.content.Context
import android.net.Uri
import com.example.midterm.model.Product
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

class ProductRepository(private val context: Context) {
    private val firestore = FirebaseFirestore.getInstance()
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
            var localPath = product.imageUrl
            if (imageUri != null) {
                localPath = saveImageLocally(imageUri) // Lưu vào máy, lấy path
            }
            val finalProduct = product.copy(imageUrl = localPath)
            productsCollection.add(finalProduct.toMap()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProduct(product: Product, imageUri: Uri?): Result<Unit> {
        return try {
            var localPath = product.imageUrl
            if (imageUri != null) {
                localPath = saveImageLocally(imageUri)
            }
            val finalProduct = product.copy(imageUrl = localPath)
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

    private fun saveImageLocally(uri: Uri): String {
        // Tạo thư mục "images" trong bộ nhớ trong của App nếu chưa có
        val imagesDir = File(context.filesDir, "images")
        if (!imagesDir.exists()) {
            imagesDir.mkdirs()
        }

        val fileName = "${UUID.randomUUID()}.jpg"
        val file = File(imagesDir, fileName)

        context.contentResolver.openInputStream(uri)?.use { inputStream ->
            FileOutputStream(file).use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }

        return file.absolutePath
    }
}