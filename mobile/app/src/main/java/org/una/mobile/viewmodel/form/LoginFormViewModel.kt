package org.una.mobile.viewmodel.form

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class LoginFormViewModel : ViewModel() {
    var username: String by mutableStateOf("")
    var password: String by mutableStateOf("")

    val isValid: Boolean by derivedStateOf { username.isNotBlank() && password.isNotBlank() }
}