package org.una.mobile.ui.layout

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.serialization.Serializable
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.una.mobile.R
import org.una.mobile.ui.layout.SnackbarState.Type.*

val snackbarHost: @Composable (SnackbarHostState) -> Unit = {
    SnackbarHost(it) { data ->
        Snackbar(Json.decodeFromString(data.message))
    }
}

@Immutable
@Serializable
data class SnackbarState(val type: Type, val message: String) {
    enum class Type { DEFAULT, INFORMATION, WARNING, ERROR }
}

@Composable
private fun Snackbar(
    state: SnackbarState,
    modifier: Modifier = Modifier,
    action: @Composable (() -> Unit)? = null,
    actionOnNewLine: Boolean = false,
    shape: Shape = MaterialTheme.shapes.small,
    elevation: Dp = 6.dp,
) {
    val backgroundColor: Color = when (state.type) {
        DEFAULT -> SnackbarDefaults.backgroundColor
        INFORMATION -> Color.LightGray
        WARNING -> Color.Yellow
        ERROR -> MaterialTheme.colors.error
    }
    val contentColor: Color = when (state.type) {
        INFORMATION -> Color.Black
        WARNING -> Color.Black
        else -> contentColorFor(backgroundColor)
    }
    val icon: ImageVector? = when (state.type) {
        DEFAULT -> null
        INFORMATION -> Icons.Filled.Info
        WARNING -> Icons.Filled.Warning
        ERROR -> Icons.Filled.Error
    }
    val description: String? = when (state.type) {
        DEFAULT -> null
        INFORMATION -> stringResource(R.string.snackbar_information_description)
        WARNING -> stringResource(R.string.snackbar_warning_description)
        ERROR -> stringResource(R.string.snackbar_error_description)
    }

    Snackbar(modifier, action, actionOnNewLine, shape, backgroundColor, contentColor, elevation) {
        Row {
            if (icon != null) {
                Icon(icon, description, Modifier.padding(end = 16.dp))
            }
            Text(state.message)
        }
    }
}