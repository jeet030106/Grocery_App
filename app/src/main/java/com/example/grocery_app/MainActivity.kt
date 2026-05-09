package com.example.grocery_app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.grocery_app.ui.features.cart.CartScreen
import com.example.grocery_app.ui.features.home.HomeScreen
import com.example.grocery_app.ui.features.login.LoginScreen
import com.example.grocery_app.ui.features.navigation.NavRoutes
import com.example.grocery_app.ui.features.payment.PaymentScreen
import com.example.grocery_app.ui.theme.Grocery_AppTheme
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Grocery_AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()

                    NavHost(
                        navController = navController,
                        startDestination = NavRoutes.Login
                    ) {

                        composable<NavRoutes.Login> {
                            LoginScreen(
                                onNavigateToHome = {
                                    navController.navigate(NavRoutes.Home) {
                                        popUpTo(NavRoutes.Login) {
                                            inclusive = true
                                        }
                                    }
                                }
                            )
                        }
                        composable<NavRoutes.Home> {
                            HomeScreen(
                                onNavigateToCart = {
                                    navController.navigate(NavRoutes.Cart)
                                }
                            )
                        }

                        composable<NavRoutes.Cart> {
                            CartScreen(
                                onNavigateBack = {navController.popBackStack()},
                                onNavigateToCheckout ={
                                    navController.navigate(NavRoutes.Payment)
                                }
                            )
                        }

                        composable<NavRoutes.Payment> {
                            PaymentScreen(
                                onNavigateBack = {navController.popBackStack()},
                                onPaymentSuccessful = {navController.navigate(NavRoutes.Home)}
                            )
                        }
                    }
                }
            }
        }
    }
}