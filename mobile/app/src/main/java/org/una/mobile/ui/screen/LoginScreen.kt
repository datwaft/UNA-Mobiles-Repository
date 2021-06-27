package org.una.mobile.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.una.mobile.R
import org.una.mobile.ui.components.GenericTextField
import org.una.mobile.ui.components.PasswordTextField
import org.una.mobile.ui.theme.Theme
import org.una.mobile.viewmodel.form.LoginFormViewModel

@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit,
    onLogin: (username: String, password: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LoginFormViewModel = viewModel(),
) {
    LoginForm(
        viewModel.username, { viewModel.username = it },
        viewModel.password, { viewModel.password = it },
        viewModel.isValid,
        onNavigateToRegister,
        onSubmit = { onLogin(viewModel.username, viewModel.password) },
        modifier)
}

@Composable
private fun LoginForm(
    username: String, onUsernameChange: (String) -> Unit,
    password: String, onPasswordChange: (String) -> Unit,
    isValid: Boolean,
    onNavigateToRegister: () -> Unit,
    onSubmit: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .padding(16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        GenericTextField(username, onUsernameChange,
            label = stringResource(R.string.field_username_label),
            modifier = Modifier.fillMaxWidth())
        PasswordTextField(password, onPasswordChange,
            modifier = Modifier.fillMaxWidth())

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            OutlinedButton(onClick = onNavigateToRegister) {
                Text(stringResource(R.string.action_register_label).uppercase())
            }
            Button(onClick = onSubmit, enabled = isValid) {
                Text(stringResource(R.string.action_login_label).uppercase())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isValid by remember(username, password) {
        derivedStateOf { username.isNotBlank() && password.isNotBlank() }
    }

    Theme {
        LoginForm(
            username, { username = it },
            password, { password = it },
            isValid,
            onNavigateToRegister = { Log.d("PREVIEW", "Emitted navigateToRegister event") },
            onSubmit = { Log.d("PREVIEW", "Emitted login event") })
    }
}