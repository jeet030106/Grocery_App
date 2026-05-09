package com.example.grocery_app.ui.features.payment

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.grocery_app.ui.theme.Primary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    viewModel: PaymentViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
    onPaymentSuccessful: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (uiState !is PaymentUiState.Success) {
                        Text("Payment", fontWeight = FontWeight.Bold)
                    }
                },
                navigationIcon = {
                    if (uiState !is PaymentUiState.Success && uiState !is PaymentUiState.Processing) {
                        IconButton(onClick = onNavigateBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            if (uiState is PaymentUiState.Idle) {
                val state = uiState as PaymentUiState.Idle
                Surface(color = Color.White, shadowElevation = 8.dp) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = { viewModel.processPayment() },
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text(
                                text = "Pay ₹${state.amountToPay.toInt()}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                        }
                    }
                }
            } else if (uiState is PaymentUiState.Success) {
                Surface(color = Color.White, shadowElevation = 8.dp) {
                    Box(modifier = Modifier.padding(16.dp)) {
                        Button(
                            onClick = onPaymentSuccessful,
                            colors = ButtonDefaults.buttonColors(containerColor = Primary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text("Back to Home", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        }
                    }
                }
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
                is PaymentUiState.Loading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center), color = Primary)
                }
                is PaymentUiState.Error -> {
                    Text(text = state.message, color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
                }
                is PaymentUiState.Processing -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = Primary, modifier = Modifier.size(60.dp))
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Processing Payment...", fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Text("Please do not close the app or press back.", fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(top = 8.dp))
                    }
                }
                is PaymentUiState.Success -> {
                    Column(
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .padding(top = 60.dp, start = 24.dp, end = 24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Success",
                            tint = Primary,
                            modifier = Modifier.size(80.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Order Placed Successfully!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        // Order Details Card
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = Color.White),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Column(modifier = Modifier.padding(20.dp)) {
                                Text("Order Summary", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 16.dp))

                                OrderDetailRow(label = "Order ID", value = state.orderId)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE0E0E0))

                                OrderDetailRow(label = "Items", value = state.itemSummary)
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE0E0E0))

                                OrderDetailRow(label = "Total Paid", value = "₹${state.totalAmount.toInt()}")
                                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = Color(0xFFE0E0E0))

                                OrderDetailRow(
                                    label = "Expected Delivery",
                                    value = state.deliveryTime,
                                    valueColor = Primary,
                                    valueWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
                is PaymentUiState.Idle -> {
                    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                        Text("Select Payment Method", fontWeight = FontWeight.Bold, fontSize = 18.sp, modifier = Modifier.padding(bottom = 16.dp))
                        val paymentMethods = listOf("UPI", "Credit / Debit Card", "Net Banking", "Cash on Delivery")
                        paymentMethods.forEach { method ->
                            PaymentMethodCard(
                                title = method,
                                isSelected = state.selectedMethod == method,
                                onClick = { viewModel.selectPaymentMethod(method) }
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun OrderDetailRow(
    label: String,
    value: String,
    valueColor: Color = Color.Black,
    valueWeight: FontWeight = FontWeight.Normal
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.Gray, fontSize = 15.sp)
        Text(text = value, color = valueColor, fontWeight = valueWeight, fontSize = 15.sp)
    }
}

@Composable
fun PaymentMethodCard(title: String, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFFE8F5E9) else Color.White),
        border = if (isSelected) BorderStroke(1.dp, Primary) else BorderStroke(1.dp, Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(selected = isSelected, onClick = onClick, colors = RadioButtonDefaults.colors(selectedColor = Primary))
            Spacer(modifier = Modifier.width(12.dp))
            Text(text = title, fontSize = 16.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, color = if (isSelected) Primary else Color.Black)
        }
    }
}