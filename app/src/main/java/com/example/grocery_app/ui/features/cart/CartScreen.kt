package com.example.grocery_app.ui.features.cart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage // <-- ADDED COIL IMPORT
import com.example.grocery_app.data.room.CartItem
import com.example.grocery_app.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    viewModel: CartViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onNavigateToCheckout: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Cart ", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (uiState is CartUiState.Success) {
                val state = uiState as CartUiState.Success
                BottomCheckoutBar(
                    grandTotal = state.grandTotal,
                    onCheckoutClick = onNavigateToCheckout
                )
            }
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (val state = uiState) {
                is CartUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Primary
                    )
                }

                is CartUiState.Empty -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_myplaces),
                            contentDescription = "Empty Cart",
                            modifier = Modifier.size(100.dp),
                            tint = Color.LightGray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Your cart is empty",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )
                    }
                }

                is CartUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        item {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = Color.White),
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = "Delivery in 10 minutes",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        modifier = Modifier.padding(bottom = 16.dp)
                                    )

                                    state.cartItems.forEachIndexed { index, item ->
                                        CartItemRow(
                                            item = item,
                                            onUpdateQuantity = { newQty ->
                                                viewModel.updateQuantity(item, newQty)
                                            }
                                        )
                                        if (index < state.cartItems.size - 1) {
                                            HorizontalDivider(
                                                modifier = Modifier.padding(vertical = 12.dp),
                                                color = Color(0xFFE0E0E0)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        item {
                            BillDetailsCard(
                                itemTotal = state.itemTotal,
                                deliveryFee = state.deliveryFee,
                                grandTotal = state.grandTotal
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CartItemRow(item: CartItem, onUpdateQuantity: (Int) -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // --- CHANGED TO ASYNCIMAGE ---
        AsyncImage(
            model = item.imageUrl, // Make sure your CartItem entity has an imageUrl property!
            contentDescription = item.name,
            modifier = Modifier
                .size(60.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = item.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(text = item.unit, color = Color.Gray, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "₹${item.price.toInt()}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
        }

        Row(
            modifier = Modifier
                .background(Color.White, RoundedCornerShape(8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "—",
                fontSize = 18.sp,
                color = Primary,
                modifier = Modifier
                    .clickable { onUpdateQuantity(item.quantity - 1) }
                    .padding(8.dp)
            )
            Text(
                text = item.quantity.toString(),
                fontWeight = FontWeight.Bold,
                color = Primary,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "Increase",
                tint = Primary,
                modifier = Modifier
                    .clickable { onUpdateQuantity(item.quantity + 1) }
                    .padding(8.dp)
            )
        }
    }
}

@Composable
fun BillDetailsCard(itemTotal: Double, deliveryFee: Double, grandTotal: Double) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Bill Details",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Item Total", color = Color.Gray, fontSize = 14.sp)
                Text(text = "₹${itemTotal.toInt()}", fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Delivery Fee", color = Color.Gray, fontSize = 14.sp)
                Text(text = if (deliveryFee == 0.0) "FREE" else "₹${deliveryFee.toInt()}", color = if (deliveryFee == 0.0) Primary else Color.Black, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE0E0E0))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "Grand Total", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(text = "₹${grandTotal.toInt()}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun BottomCheckoutBar(grandTotal: Double, onCheckoutClick: () -> Unit) {
    Surface(
        color = Color.White,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = "₹${grandTotal.toInt()}", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "TOTAL", color = Primary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Button(
                onClick = onCheckoutClick,
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .width(200.dp)
                    .height(50.dp)
            ) {
                Text("Place Order", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}