package com.example.grocery_app.data.remote

import com.example.grocery_app.data.room.OrderDao
import com.example.grocery_app.data.room.OrderEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val orderDao: OrderDao
) {
    suspend fun placeOrder(order: OrderEntity) {
        orderDao.insertOrder(order)
    }
}