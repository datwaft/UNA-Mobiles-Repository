package org.una.mobile.model

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Ticket(
    val identifier: Long,
    val row: Int,
    val column: Int,
)