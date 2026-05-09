package com.example.grocery_app.data.model

data class Product(
    val id: Int,
    val categoryId: Int,
    val name: String,
    val price: Double,
    val unit: String, // e.g., "1 kg", "500 g", "1 Dozen"
    val imageUrl: String
)