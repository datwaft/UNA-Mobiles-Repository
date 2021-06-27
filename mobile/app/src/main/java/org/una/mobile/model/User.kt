package org.una.mobile.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class User(
    val name: String,
    val lastname: String,
    val email: String,
    val address: String,
    val workphone: String,
    val mobilephone: String,
) {
    val fullName: String
        get() = "$name $lastname"
}