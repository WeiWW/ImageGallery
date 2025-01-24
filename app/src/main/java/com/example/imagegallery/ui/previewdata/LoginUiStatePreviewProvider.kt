package com.example.imagegallery.ui.previewdata

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.example.imagegallery.ui.login.LoginUiState

class LoginUiStatePreviewProvider: PreviewParameterProvider<LoginUiState> {
    override val values = sequenceOf(
        LoginUiState(
            username = "username",
            password = "password",
            isValidUsrName = true,
            isValidPwd = true
        ),
        LoginUiState(
            username = "username",
            password = "password",
            isValidUsrName = false,
            isValidPwd = true
        ),
        LoginUiState(
            username = "username",
            password = "password",
            isValidUsrName = true,
            isValidPwd = false
        ),
        LoginUiState(
            username = "username",
            password = "password",
            isValidUsrName = false,
            isValidPwd = false
        ),
        LoginUiState(
            username = "username",
            password = "password",
            isValidUsrName = true,
            isValidPwd = true,
            loginSuccess = false,
            errorMessage = "Login failed. Please try again."
        ),
    )
}