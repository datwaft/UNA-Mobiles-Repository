package org.una.mobile.viewmodel.form

import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import org.una.mobile.model.User

class UserFormViewModel : ViewModel() {
    var name: String by mutableStateOf("")
    var lastname: String by mutableStateOf("")
    var email: String by mutableStateOf("")
    var address: String by mutableStateOf("")
    var mobilephone: String by mutableStateOf("")
    var workphone: String by mutableStateOf("")

    val isValid: Boolean by derivedStateOf {
        name.isNotBlank() && lastname.isNotBlank() && email.isNotBlank() &&
                address.isNotBlank() && mobilephone.isNotBlank() && workphone.isNotBlank()
    }

    fun import(user: User) {
        name = user.name
        lastname = user.lastname
        email = user.email
        address = user.address
        mobilephone = user.mobilephone
        workphone = user.workphone
    }

    fun export(): User =
        User(
            name = name,
            lastname = lastname,
            email = email,
            address = address,
            mobilephone = mobilephone,
            workphone = workphone,
        )
}