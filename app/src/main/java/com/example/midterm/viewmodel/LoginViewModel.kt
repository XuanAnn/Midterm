package com.example.midterm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midterm.repository.AuthRepository
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun login(email: String, password: String, onResult: (FirebaseUser?) -> Unit) {
        if (email.isBlank() || password.isBlank()) {
            _error.value = "Vui lòng nhập đầy đủ thông tin"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.login(email, password)
            _isLoading.value = false
            
            if (result.isSuccess) {
                onResult(result.getOrNull())
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Đăng nhập thất bại"
                onResult(null)
            }
        }
    }

    fun signUp(email: String, password: String, role: String, onResult: (Boolean) -> Unit) {
        if (email.isBlank() || password.isBlank() || role.isBlank()) {
            _error.value = "Vui lòng điền đầy đủ thông tin"
            return
        }

        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.signUp(email, password, role)
            _isLoading.value = false
            
            if (result.isSuccess) {
                onResult(true)
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Đăng ký thất bại"
                onResult(false)
            }
        }
    }

    fun clearError() {
        _error.value = null
    }
}
