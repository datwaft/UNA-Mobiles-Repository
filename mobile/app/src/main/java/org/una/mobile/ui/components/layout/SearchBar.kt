package org.una.mobile.ui.components.layout

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.una.mobile.R
import org.una.mobile.ui.theme.Theme

@Composable
fun SearchBar(value: String, onValueChange: (String) -> Unit, modifier: Modifier = Modifier) {
    OutlinedTextField(value, onValueChange, modifier.fillMaxWidth(),
        singleLine = true,
        leadingIcon = {
            Icon(Icons.Filled.Search, stringResource(R.string.action_search_description))
        },
        trailingIcon = when {
            value.isNotEmpty() -> ({
                IconButton(onClick = { onValueChange("") }) {
                    Icon(Icons.Filled.Cancel, stringResource(R.string.action_clear_description))
                }
            })
            else -> null
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    var value: String by remember { mutableStateOf("") }

    Theme {
        SearchBar(value, { value = it }, Modifier.padding(16.dp))
    }
}