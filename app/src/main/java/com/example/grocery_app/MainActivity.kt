package com.example.grocery_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.grocery_app.data.data_store.UserPreferences // Import DataStore
import com.example.grocery_app.ui.features.cart.CartScreen
import com.example.grocery_app.ui.features.home.HomeScreen
import com.example.grocery_app.ui.features.login.LoginScreen
import com.example.grocery_app.ui.features.navigation.NavRoutes
import com.example.grocery_app.ui.features.orders.OrdersScreen
import com.example.grocery_app.ui.features.payment.PaymentScreen
import com.example.grocery_app.ui.theme.Grocery_AppTheme
import com.example.grocery_app.ui.theme.Primary
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Inject UserPreferences
    @Inject lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Grocery_AppTheme {

                val isLoggedIn by userPreferences.isLoggedIn.collectAsState(initial = null)

                if (isLoggedIn != null) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        val navController = rememberNavController()
                        val showBottomBar = remember { mutableStateOf(false) }

                        Scaffold(
                            bottomBar = {
                                AnimatedVisibility(visible = showBottomBar.value) {
                                    NavigationBar(containerColor = Color.White) {
                                        val navBackStackEntry by navController.currentBackStackEntryAsState()
                                        val currentDest = navBackStackEntry?.destination

                                        val items = listOf(
                                            Triple(NavRoutes.Home, Icons.Default.Home, "Home"),
                                            Triple(NavRoutes.Orders, Icons.Default.List, "Orders")
                                        )

                                        items.forEach { (route, icon, label) ->
                                            val selected = currentDest?.hierarchy?.any {
                                                it.hasRoute(route::class)
                                            } == true

                                            NavigationBarItem(
                                                selected = selected,
                                                onClick = {
                                                    navController.navigate(route) {
                                                        popUpTo(navController.graph.findStartDestination().id) {
                                                            saveState = true
                                                        }
                                                        launchSingleTop = true
                                                        restoreState = true
                                                    }
                                                },
                                                icon = {
                                                    Icon(
                                                        imageVector = icon,
                                                        contentDescription = label,
                                                        tint = if (selected) Primary else Color.Gray
                                                    )
                                                },
                                                label = {
                                                    Text(
                                                        text = label,
                                                        color = if (selected) Primary else Color.Gray
                                                    )
                                                },
                                                colors = NavigationBarItemDefaults.colors(
                                                    selectedIconColor = Primary,
                                                    selectedTextColor = Primary,
                                                    unselectedIconColor = Color.Transparent,
                                                    unselectedTextColor = Color.Gray,
                                                    indicatorColor = Color(0xFFE8F5E9)
                                                )
                                            )
                                        }
                                    }
                                }
                            }
                        ) { innerPadding ->
                            NavHost(
                                navController = navController,

                                startDestination = if (isLoggedIn == true) NavRoutes.Home else NavRoutes.Login,
                                modifier = Modifier.padding(innerPadding)
                            ) {

                                composable<NavRoutes.Login> {
                                    LaunchedEffect(Unit) { showBottomBar.value = false }
                                    LoginScreen(
                                        onNavigateToHome = {
                                            navController.navigate(NavRoutes.Home) {
                                                popUpTo(NavRoutes.Login) { inclusive = true }
                                            }
                                        }
                                    )
                                }

                                composable<NavRoutes.Home> {
                                    LaunchedEffect(Unit) { showBottomBar.value = true }
                                    HomeScreen(
                                        onNavigateToCart = {
                                            navController.navigate(NavRoutes.Cart)
                                        }
                                    )
                                }

                                composable<NavRoutes.Orders> {
                                    LaunchedEffect(Unit) { showBottomBar.value = true }
                                    OrdersScreen()
                                }

                                composable<NavRoutes.Cart> {
                                    LaunchedEffect(Unit) { showBottomBar.value = false }
                                    CartScreen(
                                        onNavigateBack = { navController.popBackStack() },
                                        onNavigateToCheckout = {
                                            navController.navigate(NavRoutes.Payment)
                                        }
                                    )
                                }

                                composable<NavRoutes.Payment> {
                                    LaunchedEffect(Unit) { showBottomBar.value = false }
                                    PaymentScreen(
                                        onNavigateBack = { navController.popBackStack() },
                                        onPaymentSuccessful = {
                                            navController.navigate(NavRoutes.Home) {
                                                popUpTo(NavRoutes.Home) { inclusive = true }
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}