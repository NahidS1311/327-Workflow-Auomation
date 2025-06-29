package com.example.quickflow.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.quickflow.auth.AuthService
import com.example.quickflow.auth.AuthResult
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    
    private val authService = AuthService(application)
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _currentUserId = MutableStateFlow<String?>(null)
    val currentUserId: StateFlow<String?> = _currentUserId.asStateFlow()
    
    init {
        checkAuthState()
    }
    
    private fun checkAuthState() {
        viewModelScope.launch {
            try {
                val isLoggedIn = authService.isUserLoggedIn()
                if (isLoggedIn) {
                    val userId = authService.getCurrentUser()
                    _currentUserId.value = userId
                    _authState.value = AuthState.Authenticated(userId ?: "")
                } else {
                    _authState.value = AuthState.Unauthenticated
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Failed to check authentication state")
            }
        }
    }
    
    fun signInWithGoogle(task: Task<GoogleSignInAccount>) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                when (val result = authService.handleSignInResult(task)) {
                    is AuthResult.Success -> {
                        _currentUserId.value = result.userId
                        _authState.value = AuthState.Authenticated(result.userId)
                    }
                    is AuthResult.Error -> {
                        _authState.value = AuthState.Error(result.message)
                    }
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Sign in failed: ${e.message}")
            }
        }
    }
    
    fun signOut() {
        viewModelScope.launch {
            try {
                authService.signOut()
                _currentUserId.value = null
                _authState.value = AuthState.Unauthenticated
            } catch (e: Exception) {
                _authState.value = AuthState.Error("Sign out failed: ${e.message}")
            }
        }
    }
    
    fun getSignInIntent() = authService.getSignInIntent()
}

sealed class AuthState {
    object Loading : AuthState()
    data class Authenticated(val userId: String) : AuthState()
    object Unauthenticated : AuthState()
    data class Error(val message: String) : AuthState()
} 