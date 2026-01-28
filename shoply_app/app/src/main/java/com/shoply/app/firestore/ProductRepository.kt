package com.shoply.app.firestore

import com.google.firebase.firestore.FirebaseFirestore
import com.shoply.app.data.Product

class ProductRepository {

    private val db = FirebaseFirestore.getInstance()
    private val productsRef = db.collection("products")

    fun addProduct(product: Product, onResult: (Boolean) -> Unit) {
        productsRef.add(product)
            .addOnSuccessListener { onResult(true) }
            .addOnFailureListener { onResult(false) }
    }

    fun getProducts(onResult: (List<Product>) -> Unit) {
        productsRef.get()
            .addOnSuccessListener { result ->
                val products = result.documents.mapNotNull {
                    it.toObject(Product::class.java)
                }
                onResult(products)
            }
    }
}
