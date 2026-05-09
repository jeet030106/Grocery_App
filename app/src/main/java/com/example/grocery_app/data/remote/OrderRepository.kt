package com.example.grocery_app.data.remote

import com.example.grocery_app.data.room.OrderDao
import com.example.grocery_app.data.room.OrderEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderRepository @Inject constructor(
    private val orderDao: OrderDao
) {
    suspend fun placeOrder(order: OrderEntity) {
        orderDao.insertOrder(order)
        CoroutineScope(Dispatchers.IO).launch{
            delay(60000)
            orderDao.markOrderAsDelivered(order.orderId)
        }
    }

    fun getAllOrders(): Flow<List<OrderEntity>> {
        return orderDao.getAllOrders()
    }
}