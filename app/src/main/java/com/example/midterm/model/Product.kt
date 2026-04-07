package com.example.midterm.model

data class Product(
    val id: String = "",
    val name: String = "",
    val price: String = "",
    val description: String = "",
    val imageUrl: String = ""
){
    fun toMap(): Map<String, Any?> {
        return mapOf(
            "name" to name,
            "price" to price,
            "description" to description,
            "imageUrl" to imageUrl
        )
    }
}
