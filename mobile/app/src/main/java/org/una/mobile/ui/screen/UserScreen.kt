package org.una.mobile.ui.screen

import android.util.Log
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.una.mobile.R
import org.una.mobile.ui.components.AddressTextField
import org.una.mobile.ui.components.EmailTextField
import org.una.mobile.ui.components.GenericTextField
import org.una.mobile.ui.components.PhoneTextField
import org.una.mobile.ui.theme.Theme
import org.una.mobile.viewmodel.form.UserFormViewModel

@Composable
fun UserScreen(
    onUpdate: (name: String, lastname: String, email: String, address: String, mobilephone: String, workphone: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: UserFormViewModel = viewModel(),
) {
    UserForm(
        viewModel.name, { viewModel.name = it },
        viewModel.lastname, { viewModel.lastname = it },
        viewModel.email, { viewModel.email = it },
        viewModel.address, { viewModel.address = it },
        viewModel.mobilephone, { viewModel.mobilephone = it },
        viewModel.workphone, { viewModel.workphone = it },
        viewModel.isValid,
        onSubmit = {
            onUpdate(
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
private fun UserForm(
    name: String, onNameChange: (String) -> Unit,
    lastname: String, onLastnameChange: (String) -> Unit,
    email: String, onEmailChange: (String) -> Unit,
    address: String, onAddressChange: (String) -> Unit,
    mobilephone: String, onMobilephoneChange: (String) -> Unit,
    workphone: String, onWorkphoneChange: (String) -> Unit,
    isValid: Boolean,
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

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            Button(onClick = onSubmit, enabled = isValid) {
                Text(stringResource(R.string.action_update_label).uppercase())
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    var name by remember { mutableStateOf("") }
    var lastname by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var mobilePhone by remember { mutableStateOf("") }
    var workPhone by remember { mutableStateOf("") }
    val isValid by remember {
        derivedStateOf {
            name.isNotBlank() && lastname.isNotBlank() && email.isNotBlank() &&
                    address.isNotBlank() && mobilePhone.isNotBlank() && workPhone.isNotBlank()
        }
    }

    Theme {
        Surface(color = MaterialTheme.colors.background) {
            UserForm(name, { name = it },
                lastname, { lastname = it },
                email, { email = it },
                address, { address = it },
                mobilePhone, { mobilePhone = it },
                workPhone, { workPhone = it },
                isValid,
                onSubmit = { Log.d("PREVIEW", "Emitted updated event") })
        }
    }
}