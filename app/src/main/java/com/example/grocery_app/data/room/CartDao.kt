package com.example.grocery_app.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow


@Dao
interface CartDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(cartItem: CartItem)

    @Update
    suspend fun update(cartItem: CartItem)

    @Delete
    suspend fun delete(cartItem: CartItem)

    @Query("SELECT * FROM cart_items")
    fun getAllCartItems(): Flow<List<CartItem>>

    @Query("DELETE FROM cart_items")
    suspend fun clearCart()
}