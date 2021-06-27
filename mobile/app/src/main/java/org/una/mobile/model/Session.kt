package org.una.mobile.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Session(val username: String, val token: String) {
    override fun equals(other: Any?): Boolean =
        when (other) {
            is Session -> this.username == other.username
            else -> super.equals(other)
        }

    override fun hashCode(): Int {
        var result = username.hashCode()
        result = 31 * result + token.hashCode()
        return result
    }
}