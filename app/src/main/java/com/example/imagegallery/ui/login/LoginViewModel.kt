package com.example.imagegallery.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.imagegallery.data.common.Result
import com.example.imagegallery.data.repository.token.TokenRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val tokenRepository: TokenRepository

) : ViewModel() {
    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState = _uiState.asStateFlow()

    fun onUsernameChange(newUsername: String) {
        _uiState.update {
            it.copy(
                username = newUsername,
                isValidUsrName = isValidInput(newUsername),
            )
        }
    }

    fun onPasswordChange(newPassword: String) {
        _uiState.update {
            it.copy(
                password = newPassword,
                isValidPwd = isValidInput(newPassword),
            )
        }
    }

    fun onLoginClick() {
        viewModelScope.launch {
            val result = tokenRepository.login(uiState.value.username, uiState.value.password)
            if (result is Result.Success) {
                _uiState.update { it.copy(loginSuccess = true) }
            } else {
                _uiState.update { it.copy(errorMessage = "Login failed. Please try again.") }
            }
        }
    }

    private fun isValidInput(input: String): Boolean {
        val regex = "^[a-zA-Z0-9_]{8,}$".toRegex()
        return regex.matches(input)
    }
}

data class LoginUiState(
    val username: String = "",
    val isValidUsrName:Boolean = false,
    val password: String = "",
    val isValidPwd:Boolean = false,
    val errorMessage: String? = null,
    val loginSuccess: Boolean = false
)