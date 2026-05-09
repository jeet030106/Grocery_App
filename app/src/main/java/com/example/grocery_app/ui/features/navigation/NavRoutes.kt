package com.example.grocery_app.ui.features.navigation

import kotlinx.serialization.Serializable


@Serializable
sealed interface NavRoutes{

    @Serializable
    object Login : NavRoutes

    @Serializable
    object Home : NavRoutes

    @Serializable
    object Cart : NavRoutes

    @Serializable
    object Payment : NavRoutes

    @Serializable
    object Orders : NavRoutes
}