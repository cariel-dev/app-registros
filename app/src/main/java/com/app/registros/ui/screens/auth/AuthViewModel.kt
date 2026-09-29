package com.app.registros.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.registros.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val errorMessage: String? = null,
    val username: String? = null,
    val userId: String? = null
)

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkSession()
    }

    fun checkSession() {
        if (authRepository.isUserLoggedIn()) {
            _uiState.value = _uiState.value.copy(
                isAuthenticated = true,
                username = authRepository.getCurrentUsername(),
                userId = authRepository.getCurrentUserId()
            )
        }
    }

    fun signIn(username: String, pass: String) {
        val cleanUser = username.trim()
        if (cleanUser.isBlank() || pass.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Ingresa usuario y contraseña")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.signIn(cleanUser, pass)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    username = cleanUser,
                    userId = authRepository.getCurrentUserId()
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Credenciales incorrectas o error de conexión"
                )
            }
        }
    }

    fun signUp(username: String, pass: String) {
        val cleanUser = username.trim()
        if (cleanUser.length < 3) {
            _uiState.value = _uiState.value.copy(errorMessage = "El usuario debe tener al menos 3 caracteres")
            return
        }
        if (pass.length < 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres")
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = authRepository.signUp(cleanUser, pass)
            result.onSuccess {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    isAuthenticated = true,
                    username = cleanUser,
                    userId = authRepository.getCurrentUserId()
                )
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.localizedMessage ?: "No se pudo registrar el usuario"
                )
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOut()
            _uiState.value = AuthUiState(isAuthenticated = false)
        }
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
