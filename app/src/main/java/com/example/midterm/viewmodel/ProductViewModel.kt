package com.example.midterm.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midterm.model.Product
import com.example.midterm.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProductViewModel : ViewModel() {
    private val repository = ProductRepository()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        fetchProducts()
    }

    private fun fetchProducts() {
        viewModelScope.launch {
            repository.getProducts().collect { productList ->
                _products.value = productList
            }
        }
    }

    fun addProduct(product: Product, imageUri: Uri?, onComplete: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.addProduct(product, imageUri)
            _isLoading.value = false
            onComplete(result)
        }
    }

    fun updateProduct(product: Product, imageUri: Uri?, onComplete: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.updateProduct(product, imageUri)
            _isLoading.value = false
            onComplete(result)
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            repository.deleteProduct(productId)
        }
    }
}
