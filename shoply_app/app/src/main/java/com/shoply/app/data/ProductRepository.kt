package com.shoply.app.data

import com.shoply.app.network.RetrofitClient

class ProductRepository {

    suspend fun getProducts(): List<ShoppingItem> {
        return RetrofitClient.api.getProducts()
    }

    suspend fun getProductsByCategory(category: String): List<ShoppingItem> {
        return RetrofitClient.api.getProductsByCategory(category)
    }

    suspend fun searchProducts(search: String): List<ShoppingItem> {
        return RetrofitClient.api.searchProducts(search)
    }

    suspend fun getProductById(id: String): ShoppingItem {
        return RetrofitClient.api.getProductById(id)
    }

    suspend fun getCategories(): List<String> {
        return RetrofitClient.api.getCategories()
    }

    fun addProduct(product: ShoppingItem, onResult: (Boolean) -> Unit) {
        // מוצרים לא נשמרים ב-Firebase. מקור האמת הוא ה-API.
        onResult(false)
    }
}