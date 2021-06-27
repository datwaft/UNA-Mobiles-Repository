package org.una.mobile.ui.components

import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import org.una.mobile.R

@Composable
fun GenericTextField(
    value: String, onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    isError: Boolean = false,
) {
    OutlinedTextField(
        value, onValueChange,
        modifier = modifier,
        readOnly = readOnly,
        isError = isError,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
    )
}

@Composable
fun NumericTextField(
    value: String, onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    isError: Boolean = false,
) {
    OutlinedTextField(
        value, onValueChange,
        modifier = modifier,
        readOnly = readOnly,
        isError = isError,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
    )
}

@Composable
fun PasswordTextField(
    value: String, onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.field_password_label),
    readOnly: Boolean = false,
    isError: Boolean = false,
) {
    var hidden by remember { mutableStateOf(true) }
    val transformation = if (hidden) PasswordVisualTransformation() else VisualTransformation.None

    OutlinedTextField(
        value, onValueChange,
        modifier = modifier,
        readOnly = readOnly,
        isError = isError,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
        visualTransformation = transformation,
        trailingIcon = {
            IconButton(onClick = { hidden = !hidden }) {
                Icon(
                    imageVector = when {
                        hidden -> Icons.Filled.Visibility
                        else -> Icons.Filled.VisibilityOff
                    },
                    contentDescription = stringResource(when {
                        hidden -> R.string.action_password_show_description
                        else -> R.string.action_password_hide_description
                    })
                )
            }
        }
    )
}

@Composable
fun EmailTextField(
    value: String, onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.field_email_label),
    readOnly: Boolean = false,
    isError: Boolean = false,
) {
    OutlinedTextField(
        value, onValueChange,
        modifier = modifier,
        readOnly = readOnly,
        isError = isError,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
    )
}

@Composable
fun AddressTextField(
    value: String, onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = stringResource(R.string.field_address_label),
    readOnly: Boolean = false,
    isError: Boolean = false,
) {
    OutlinedTextField(
        value, onValueChange,
        modifier = modifier,
        readOnly = readOnly,
        isError = isError,
        label = { Text(label) },
        singleLine = false,
        maxLines = 2,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Text),
    )
}

@Composable
fun PhoneTextField(
    value: String, onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    readOnly: Boolean = false,
    isError: Boolean = false,
) {
    OutlinedTextField(
        value, onValueChange,
        modifier = modifier,
        readOnly = readOnly,
        isError = isError,
        label = { Text(label) },
        singleLine = true,
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Phone),
    )
}