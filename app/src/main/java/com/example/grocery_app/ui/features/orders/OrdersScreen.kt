package com.example.grocery_app.ui.features.orders

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.grocery_app.data.room.OrderEntity
import com.example.grocery_app.ui.theme.Primary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    viewModel: OrdersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        containerColor = Color(0xFFF5F5F5)
    ) { paddingValues ->
        Box(modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)) {

            when (val state = uiState) {
                is OrdersUiState.Loading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = Primary
                    )
                }

                is OrdersUiState.Empty -> {
                    Text(
                        text = "No orders placed yet.",
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                is OrdersUiState.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // --- Pending Orders Section ---
                        if (state.pendingOrders.isNotEmpty()) {
                            item {
                                Text(
                                    text = "To Be Delivered",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Primary,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                            }
                            items(state.pendingOrders) { order ->
                                OrderCard(order)
                            }
                        }

                        // --- Delivered Orders Section ---
                        if (state.deliveredOrders.isNotEmpty()) {
                            item {
                                Text(
                                    text = "Past Orders",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp,
                                    color = Color.Gray,
                                    modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                                )
                            }
                            items(state.deliveredOrders) { order ->
                                OrderCard(order)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderCard(order: OrderEntity) {
    val dateFormatter = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
    val formattedDate = dateFormatter.format(Date(order.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Order #${order.orderId}", fontWeight = FontWeight.Bold, fontSize = 16.sp)

                // Status Badge Colors based on pending/delivered
                val (bgColor, textColor) = if (order.status == "PENDING") {
                    Color(0xFFFFF3E0) to Color(0xFFE65100) // Orange for Pending
                } else {
                    Color(0xFFE8F5E9) to Primary // Green for Delivered
                }

                Surface(color = bgColor, shape = RoundedCornerShape(16.dp)) {
                    Text(
                        text = order.status,
                        color = textColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(text = formattedDate, color = Color.Gray, fontSize = 14.sp)

            HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE0E0E0))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = order.itemSummary, color = Color.DarkGray, fontSize = 14.sp)
                Text(
                    text = "₹${order.totalAmount.toInt()}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Primary
                )
            }
        }
    }
}