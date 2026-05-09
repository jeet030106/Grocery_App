package com.example.grocery_app.ui.features.home


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.grocery_app.data.model.Category
import com.example.grocery_app.data.model.Product
import com.example.grocery_app.data.remote.CartRepository
import com.example.grocery_app.data.remote.ProductRepository
import com.example.grocery_app.data.room.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    object Loading : HomeUiState
    data class Success(
        val searchQuery: String = "",
        val selectedCategoryId: Int = 0,
        val categories: List<Category> = emptyList(),
        val products: List<Product> = emptyList()
    ) : HomeUiState

    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val cartRepository: CartRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    val cartItems: StateFlow<List<CartItem>> = cartRepository.allCartItems
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        loadInitialData()
    }

    fun loadInitialData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                delay(800)
                val allCategory = Category(id = 0, name = "All", imageUrl = "")
                val fetchedCategories = listOf(allCategory) + productRepository.getCategories()
                val fetchedProducts = productRepository.getProducts()
                _uiState.value = HomeUiState.Success(
                    categories = fetchedCategories,
                    products = fetchedProducts
                )
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error("Failed to load products. Please try again.")
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            _uiState.value = currentState.copy(searchQuery = query)
            filterProducts()
        }
    }

    fun onCategorySelected(categoryId: Int) {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            _uiState.value = currentState.copy(selectedCategoryId = categoryId)
            filterProducts()
        }
    }

    private fun filterProducts() {
        val currentState = _uiState.value
        if (currentState is HomeUiState.Success) {
            var filteredList = if (currentState.selectedCategoryId == 0) {
                productRepository.getProducts()
            } else {
                productRepository.getProductsByCategory(currentState.selectedCategoryId)
            }

            if (currentState.searchQuery.isNotBlank()) {
                filteredList = filteredList.filter {
                    it.name.contains(currentState.searchQuery, ignoreCase = true)
                }
            }

            _uiState.value = currentState.copy(products = filteredList)
        }
    }

    // Database Calls
    fun addToCart(product: Product) {
        viewModelScope.launch {
            val cartItem = CartItem(
                productId = product.id,
                name = product.name,
                price = product.price,
                unit = product.unit,
                imageUrl = product.imageUrl,
                quantity = 1
            )
            cartRepository.addToCart(cartItem)
        }
    }

    fun updateCartQuantity(cartItem: CartItem, newQuantity: Int) {
        viewModelScope.launch {
            cartRepository.updateQuantity(cartItem.copy(quantity = newQuantity))
        }
    }
}