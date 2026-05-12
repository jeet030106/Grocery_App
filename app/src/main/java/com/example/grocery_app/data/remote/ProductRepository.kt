package com.example.grocery_app.data.remote

import com.example.grocery_app.data.model.Category
import com.example.grocery_app.data.model.Product

class ProductRepository {

    fun getCategories(): List<Category> {
        return listOf(
            Category(id = 1, name = "Fruits & Veggies", imageUrl = "https://images.unsplash.com/photo-1610832958506-aa56368176cf?w=400"),
            Category(id = 2, name = "Dairy & Breakfast", imageUrl = "https://images.unsplash.com/photo-1628088062854-d1870b455389?w=400"),
            Category(id = 3, name = "Snacks & Munchies", imageUrl = "https://images.unsplash.com/photo-1621939514649-280e2ee25f60?w=400"),
            Category(id = 4, name = "Cold Drinks", imageUrl = "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=400")
        )
    }

    fun getProducts(): List<Product> {
        return listOf(
            // Category 1: Fruits & Veggies
            Product(id = 101, categoryId = 1, name = "Fresh Bananas", price = 40.0, unit = "500 g", imageUrl = "https://images.unsplash.com/photo-1603833665858-e61d17a86224?w=400"),
            Product(id = 102, categoryId = 1, name = "Red Apples", price = 120.0, unit = "1 kg", imageUrl = "https://images.unsplash.com/photo-1560806887-1e4cd0b6fac6?w=400"),
            Product(id = 103, categoryId = 1, name = "Onions", price = 30.0, unit = "1 kg", imageUrl = "https://images.unsplash.com/photo-1618512496248-a07fe83aa8cb?w=400"),
            Product(id = 104, categoryId = 1, name = "Tomatoes", price = 45.0, unit = "500 g", imageUrl = "https://images.unsplash.com/photo-1592924357228-91a4daadcfea?w=400"),


            Product(id = 201, categoryId = 2, name = "Full Cream Milk", price = 66.0, unit = "1 L", imageUrl = "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=400"),
            Product(id = 202, categoryId = 2, name = "White Bread", price = 50.0, unit = "400 g", imageUrl = "https://images.unsplash.com/photo-1598373182133-52452f7691ef?w=400"),
            Product(id = 203, categoryId = 2, name = "Farm Eggs", price = 85.0, unit = "6 Pack", imageUrl = "https://images.unsplash.com/photo-1582722872445-44dc5f7e3c8f?w=400"),
            Product(id = 204, categoryId = 2, name = "Salted Butter", price = 56.0, unit = "100 g", imageUrl = "https://images.unsplash.com/photo-1588195538326-c5b1e9f80a1b?w=400"),

            Product(id = 301, categoryId = 3, name = "Potato Chips", price = 20.0, unit = "50 g", imageUrl = "https://images.unsplash.com/photo-1566478989037-eec170784d0b?w=400"),
            Product(id = 302, categoryId = 3, name = "Chocolate Bar", price = 40.0, unit = "1 Bar", imageUrl = "https://images.unsplash.com/photo-1549007994-cb92caebd54b?w=400"),
            Product(id = 303, categoryId = 3, name = "Mixed Nuts", price = 250.0, unit = "200 g", imageUrl = "https://images.unsplash.com/photo-1599598425947-33002620663b?w=400"),

            Product(id = 401, categoryId = 4, name = "Cola Cola", price = 40.0, unit = "750 ml", imageUrl = "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?w=400"),
            Product(id = 402, categoryId = 4, name = "Orange Juice", price = 110.0, unit = "1 L", imageUrl = "https://images.unsplash.com/photo-1600271886742-f049cd451bba?w=400")
        )
    }

    fun getProductsByCategory(categoryId: Int): List<Product> {
        return getProducts().filter { it.categoryId == categoryId }
    }

    fun searchProducts(query: String): List<Product> {
        if (query.isBlank()) return getProducts()
        return getProducts().filter {
            it.name.contains(query, ignoreCase = true)
        }
    }
}