package com.shoply.app.viewmodel

import androidx.lifecycle.ViewModel
import com.shoply.app.data.Product
import com.shoply.app.firestore.ProductRepository

class ProductViewModel : ViewModel() {

    private val repository = ProductRepository()

    fun loadProducts(onResult: (List<Product>) -> Unit) {
        repository.getProducts(onResult)
    }

    fun addProduct(product: Product, onResult: (Boolean) -> Unit) {
        repository.addProduct(product, onResult)
    }
}
