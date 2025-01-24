package com.example.imagegallery.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.imagegallery.R


@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        TextField(
            isError = uiState.username.isNotEmpty() && !uiState.isValidUsrName,
            value = uiState.username,
            onValueChange = { viewModel.onUsernameChange(it) },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        if (uiState.username.isNotEmpty() && !uiState.isValidUsrName) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.invalid_input_error_msg),
                color = Color.Red,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        TextField(
            isError = uiState.password.isNotEmpty() && !uiState.isValidPwd,
            value = uiState.password,
            onValueChange = { viewModel.onPasswordChange(it) },
            label = { Text("Password") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        if (uiState.password.isNotEmpty() && !uiState.isValidPwd) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.invalid_input_error_msg),
                color = Color.Red,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            enabled = uiState.isValidUsrName && uiState.isValidPwd,
            onClick = {
                keyboardController?.hide()
                viewModel.onLoginClick()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Login")
        }

        Spacer(modifier = Modifier.height(8.dp))

        uiState.errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (uiState.loginSuccess) {
            onLoginSuccess()
        }
    }
}