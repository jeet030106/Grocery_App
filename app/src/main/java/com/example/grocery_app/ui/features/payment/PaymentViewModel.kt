package com.example.grocery_app.ui.features.payment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocery_app.data.remote.CartRepository
import com.example.grocery_app.data.remote.OrderRepository

import com.example.grocery_app.data.room.OrderEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

sealed interface PaymentUiState {
    object Loading : PaymentUiState
    data class Idle(
        val amountToPay: Double,
        val itemCount: Int,
        val selectedMethod: String = "UPI"
    ) : PaymentUiState
    object Processing : PaymentUiState
    data class Success(
        val orderId: String,
        val deliveryTime: String,
        val totalAmount: Double,
        val itemSummary: String
    ) : PaymentUiState
    data class Error(val message: String) : PaymentUiState
}

@HiltViewModel
class PaymentViewModel @Inject constructor(
    private val cartRepository: CartRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow<PaymentUiState>(PaymentUiState.Loading)
    val uiState: StateFlow<PaymentUiState> = _uiState.asStateFlow()

    init {
        fetchCartDetails()
    }

    private fun fetchCartDetails() {
        viewModelScope.launch {
            cartRepository.allCartItems.collect { items ->
                if (items.isNotEmpty()) {
                    val itemTotal = items.sumOf { it.price * it.quantity }
                    val deliveryFee = if (itemTotal > 500) 0.0 else 25.0
                    val grandTotal = itemTotal + deliveryFee
                    val totalItems = items.sumOf { it.quantity }

                    if (_uiState.value is PaymentUiState.Loading) {
                        _uiState.value = PaymentUiState.Idle(
                            amountToPay = grandTotal,
                            itemCount = totalItems
                        )
                    }
                }
            }
        }
    }

    fun selectPaymentMethod(method: String) {
        val currentState = _uiState.value
        if (currentState is PaymentUiState.Idle) {
            _uiState.value = currentState.copy(selectedMethod = method)
        }
    }

    fun processPayment() {
        val currentState = _uiState.value
        if (currentState is PaymentUiState.Idle) {
            viewModelScope.launch {
                _uiState.value = PaymentUiState.Processing

                delay(2000)

                val timestamp = System.currentTimeMillis()
                val generatedOrderId = "ORD-" + timestamp.toString().takeLast(6)

                val deliveryTimestamp = timestamp + (10 * 60 * 1000)
                val timeFormatter = SimpleDateFormat("hh:mm a", Locale.getDefault())
                val estimatedDelivery = timeFormatter.format(Date(deliveryTimestamp))

                val summary = "${currentState.itemCount} items"

                val newOrder = OrderEntity(
                    orderId = generatedOrderId,
                    timestamp = timestamp,
                    status = "PENDING",
                    totalAmount = currentState.amountToPay,
                    itemSummary = summary
                )
                orderRepository.placeOrder(newOrder)

                cartRepository.clearCart()

                _uiState.value = PaymentUiState.Success(
                    orderId = generatedOrderId,
                    deliveryTime = estimatedDelivery,
                    totalAmount = currentState.amountToPay,
                    itemSummary = summary
                )
            }
        }
    }
}