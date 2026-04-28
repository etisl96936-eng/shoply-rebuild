package com.shoply.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shoply.app.data.ShoppingItem
import com.shoply.app.data.ProductRepository
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {

    private val repository = ProductRepository()

    fun loadProducts(onResult: (List<ShoppingItem>) -> Unit) {
        viewModelScope.launch {
            try {
                val products = repository.getProducts()
                onResult(products)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(emptyList())
            }
        }
    }

    fun addProduct(product: ShoppingItem, onResult: (Boolean) -> Unit) {
        // מוצרים מגיעים מה-API בלבד, לא מוסיפים אותם ל-Firebase
        repository.addProduct(product, onResult)
    }
}