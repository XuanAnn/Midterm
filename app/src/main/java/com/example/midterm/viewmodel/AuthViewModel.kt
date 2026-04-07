package com.example.midterm.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.midterm.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val role: String) : AuthState()
    data class Error(val message: String) : AuthState()
}

class AuthViewModel : ViewModel() {
    private val repository = AuthRepository()

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            val result = repository.login(email, password)
            if (result.isSuccess) {
                val user = result.getOrNull()
                if (user != null) {
                    val role = repository.getUserRole(user.uid) ?: "User"
                    _authState.value = AuthState.Success(role)
                } else {
                    _authState.value = AuthState.Error("Không tìm thấy thông tin người dùng")
                }
            } else {
                _authState.value = AuthState.Error(result.exceptionOrNull()?.message ?: "Lỗi đăng nhập")
            }
        }
    }

    fun resetState() {
        _authState.value = AuthState.Idle
    }
}