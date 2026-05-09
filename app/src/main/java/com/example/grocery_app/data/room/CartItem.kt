package com.example.grocery_app.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey
    val productId: Int,
    val name: String,
    val price: Double,
    val unit: String,
    val imageUrl: String,
    var quantity: Int
) {
    val itemTotal: Double
        get() = price * quantity
}