package org.una.mobile.viewmodel.form

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class RegisterFormViewModel : ViewModel() {
    var username: String by mutableStateOf("")
    var password: String by mutableStateOf("")
    var name: String by mutableStateOf("")
    var lastname: String by mutableStateOf("")
    var email: String by mutableStateOf("")
    var address: String by mutableStateOf("")
    var mobilephone: String by mutableStateOf("")
    var workphone: String by mutableStateOf("")

    val isValid: Boolean by derivedStateOf {
        username.isNotBlank() && password.isNotBlank() &&
                name.isNotBlank() && lastname.isNotBlank() && email.isNotBlank() &&
                address.isNotBlank() && mobilephone.isNotBlank() && workphone.isNotBlank()
    }
}