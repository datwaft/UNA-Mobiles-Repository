package org.una.mobile.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.una.mobile.R
import org.una.mobile.ui.components.*
import org.una.mobile.ui.theme.Theme
import org.una.mobile.viewmodel.form.RegisterFormViewModel

@Composable
fun RegisterScreen(
    onReturn: () -> Unit,
    onRegister: (username: String, password: String, name: String, lastname: String, email: String, address: String, mobilephone: String, workphone: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: RegisterFormViewModel = viewModel(),
) {
    RegisterForm(
        viewModel.username, { viewModel.username = it },
        viewModel.password, { viewModel.password = it },
        viewModel.name, { viewModel.name = it },
        viewModel.lastname, { viewModel.lastname = it },
        viewModel.email, { viewModel.email = it },
        viewModel.address, { viewModel.address = it },
        viewModel.mobilephone, { viewModel.mobilephone = it },
        viewModel.workphone, { viewModel.workphone = it },
        viewModel.isValid,
        onReturn,
        onSubmit = {
            onRegister(viewModel.username,
                viewModel.password,
                viewModel.name,
                viewModel.lastname,
                viewModel.email,
                viewModel.address,
                viewModel.mobilephone,
                viewModel.workphone)
        },
        modifier)
}

@Composable
private fun RegisterForm(
    username: String, onUsernameChange: (String) -> Unit,
    password: String, onPasswordChange: (String) -> Unit,
    name: String, onNameChange: (String) -> Unit,
    lastname: String, onLastnameChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    address: String, onAddressChange: (String) -> Unit,
    mobilephone: String, onMobilephoneChange: (String) -> Unit,
    workphone: String, onWorkphoneChange: (String) -> Unit,
    isValid: Boolean,
    onReturn: () -> Unit,
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
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            GenericTextField(name, onNameChange,
                label = stringResource(R.string.field_name_label),
                modifier = Modifier.weight(1f))
            GenericTextField(lastname, onLastnameChange,
                label = stringResource(R.string.field_lastname_label),
                modifier = Modifier.weight(1f))
        }
        EmailTextField(email, onEmailChange,
            modifier = Modifier.fillMaxWidth())
        AddressTextField(address, onAddressChange,
            modifier = Modifier.fillMaxWidth())
        PhoneTextField(mobilephone, onMobilephoneChange,
            label = stringResource(R.string.field_mobilephone_label),
            modifier = Modifier.fillMaxWidth())
        PhoneTextField(workphone, onWorkphoneChange,
            label = stringResource(R.string.field_workphone_label),
            modifier = Modifier.fillMaxWidth())

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
            TextButton(onClick = onReturn) {
                Text(stringResource(R.string.action_return_label).uppercase())
            }
            Button(onClick = onSubmit, enabled = isValid) {
                Text(stringResource(R.string.action_register_label).uppercase())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var name by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var mobilePhone by remember { mutableStateOf("") }
    var workPhone by remember { mutableStateOf("") }
    val isValid by remember {
        derivedStateOf {
            username.isNotBlank() && password.isNotBlank() && name.isNotBlank() &&
                    lastname.isNotBlank() && email.isNotBlank() && address.isNotBlank() &&
                    mobilePhone.isNotBlank() && workPhone.isNotBlank()
        }
    }

    Theme {
        Surface(color = MaterialTheme.colors.background) {
            RegisterForm(username, { username = it },
                password, { password = it },
                name, { name = it },
                lastname, { lastname = it },
                email, { email = it },
                address, { address = it },
                mobilePhone, { mobilePhone = it },
                workPhone, { workPhone = it },
                isValid,
                onReturn = { Log.d("PREVIEW", "Emitted return event") },
                onSubmit = { Log.d("PREVIEW", "Emitted register event") })
        }
    }
}