package com.example.grocery_app.data.remote

import com.example.grocery_app.data.model.Category
import com.example.grocery_app.data.model.Product

class ProductRepository {

    fun getCategories(): List<Category> {
        return listOf(
            Category(id = 1, name = "Fruits & Veggies", imageUrl = "https://picsum.photos/seed/fruits/200"),
            Category(id = 2, name = "Dairy & Breakfast", imageUrl = "https://picsum.photos/seed/dairy/200"),
            Category(id = 3, name = "Snacks & Munchies", imageUrl = "https://picsum.photos/seed/snacks/200"),
            Category(id = 4, name = "Cold Drinks", imageUrl = "https://picsum.photos/seed/drinks/200")
        )
    }

    fun getProducts(): List<Product> {
        return listOf(
            Product(id = 101, categoryId = 1, name = "Fresh Bananas", price = 40.0, unit = "500 g", imageUrl = "https://picsum.photos/seed/banana/200"),
            Product(id = 102, categoryId = 1, name = "Red Apples", price = 120.0, unit = "1 kg", imageUrl = "https://picsum.photos/seed/apple/200"),
            Product(id = 103, categoryId = 1, name = "Onions", price = 30.0, unit = "1 kg", imageUrl = "https://picsum.photos/seed/onion/200"),
            Product(id = 104, categoryId = 1, name = "Tomatoes", price = 45.0, unit = "500 g", imageUrl = "https://picsum.photos/seed/tomato/200"),

            Product(id = 201, categoryId = 2, name = "Full Cream Milk", price = 66.0, unit = "1 L", imageUrl = "https://picsum.photos/seed/milk/200"),
            Product(id = 202, categoryId = 2, name = "White Bread", price = 50.0, unit = "400 g", imageUrl = "https://picsum.photos/seed/bread/200"),
            Product(id = 203, categoryId = 2, name = "Farm Eggs", price = 85.0, unit = "6 Pack", imageUrl = "https://picsum.photos/seed/eggs/200"),
            Product(id = 204, categoryId = 2, name = "Salted Butter", price = 56.0, unit = "100 g", imageUrl = "https://picsum.photos/seed/butter/200"),

            Product(id = 301, categoryId = 3, name = "Potato Chips", price = 20.0, unit = "50 g", imageUrl = "https://picsum.photos/seed/chips/200"),
            Product(id = 302, categoryId = 3, name = "Chocolate Bar", price = 40.0, unit = "1 Bar", imageUrl = "https://picsum.photos/seed/choco/200"),
            Product(id = 303, categoryId = 3, name = "Mixed Nuts", price = 250.0, unit = "200 g", imageUrl = "https://picsum.photos/seed/nuts/200"),

            Product(id = 401, categoryId = 4, name = "Cola Cola", price = 40.0, unit = "750 ml", imageUrl = "https://picsum.photos/seed/cola/200"),
            Product(id = 402, categoryId = 4, name = "Orange Juice", price = 110.0, unit = "1 L", imageUrl = "https://picsum.photos/seed/juice/200")
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