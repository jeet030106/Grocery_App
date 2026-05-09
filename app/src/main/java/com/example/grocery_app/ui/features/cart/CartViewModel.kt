package com.example.grocery_app.ui.features.cart


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocery_app.data.remote.CartRepository
import com.example.grocery_app.data.room.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface CartUiState {
    object Loading : CartUiState
    object Empty : CartUiState
    data class Success(
        val cartItems: List<CartItem>,
        val itemTotal: Double,
        val deliveryFee: Double,
        val grandTotal: Double
    ) : CartUiState
}

@HiltViewModel
class CartViewModel @Inject constructor(
    private val cartRepository: CartRepository
) : ViewModel() {

    val uiState: StateFlow<CartUiState> = cartRepository.allCartItems
        .map { items ->
            if (items.isEmpty()) {
                CartUiState.Empty
            } else {
                val itemTotal = items.sumOf { it.price * it.quantity }
                val deliveryFee = if (itemTotal > 500) 0.0 else 25.0
                CartUiState.Success(
                    cartItems = items,
                    itemTotal = itemTotal,
                    deliveryFee = deliveryFee,
                    grandTotal = itemTotal + deliveryFee
                )
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = CartUiState.Loading
        )

    fun updateQuantity(cartItem: CartItem, newQuantity: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(cartItem.copy(quantity = newQuantity))
        }
    }
}