package com.example.grocery_app.data.remote


import com.example.grocery_app.data.room.CartDao
import com.example.grocery_app.data.room.CartItem
import kotlinx.coroutines.flow.Flow

class CartRepository(private val cartDao: CartDao) {

    val allCartItems: Flow<List<CartItem>> = cartDao.getAllCartItems()

    suspend fun addToCart(cartItem: CartItem) {
        cartDao.insertOrUpdate(cartItem)
    }

    suspend fun updateQuantity(cartItem: CartItem) {
        if (cartItem.quantity > 0) {
            cartDao.update(cartItem)
        } else {
            cartDao.delete(cartItem)
        }
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }
}