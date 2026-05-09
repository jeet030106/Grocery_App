package com.example.grocery_app.data.model

data class Product(
    val id: Int,
    val categoryId: Int,
    val name: String,
    val price: Double,
    val unit: String,
    val imageUrl: String
)