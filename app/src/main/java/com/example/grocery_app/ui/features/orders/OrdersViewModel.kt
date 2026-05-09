package com.example.grocery_app.ui.features.orders

import com.example.grocery_app.data.remote.OrderRepository
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocery_app.data.room.OrderEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed interface OrdersUiState {
    object Loading : OrdersUiState
    object Empty : OrdersUiState
    data class Success(
        val pendingOrders: List<OrderEntity>,
        val deliveredOrders: List<OrderEntity>
    ) : OrdersUiState
}

@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val orderRepository: OrderRepository
) : ViewModel() {

    val uiState: StateFlow<OrdersUiState> = orderRepository.getAllOrders()
        .map { orders ->
            if (orders.isEmpty()) {
                OrdersUiState.Empty
            } else {
                OrdersUiState.Success(
                    pendingOrders = orders.filter { it.status == "PENDING" },
                    deliveredOrders = orders.filter { it.status == "DELIVERED" }
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = OrdersUiState.Loading
        )
}