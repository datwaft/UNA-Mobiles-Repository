package org.una.mobile.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import org.una.mobile.R

@Composable
fun Dropdown(
    label: String,
    items: List<String>,
    placeholder: String,
    selected: String?, onSelected: (String?) -> Unit,
    modifier: Modifier = Modifier,
    cleanable: Boolean = false,
) {
    var isDropdownExpanded by remember { mutableStateOf(false) }

    Box(modifier, propagateMinConstraints = true) {
        OutlinedTextField(
            value = when (selected) {
                null, "" -> placeholder
                else -> selected
            },
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = {
                IconButton(onClick = { isDropdownExpanded = true }) {
                    Icon(when {
                        isDropdownExpanded -> Icons.Filled.ArrowDropUp
                        else -> Icons.Filled.ArrowDropDown
                    }, stringResource(R.string.action_dropdown_expand_description))
                }
            })
        DropdownMenu(expanded = isDropdownExpanded,
            onDismissRequest = { isDropdownExpanded = false }) {
            DropdownMenuItem(onClick = { onSelected(null) },
                enabled = cleanable) { Text(placeholder) }
            items.forEach { item ->
                DropdownMenuItem(onClick = { onSelected(item) }) { Text(item) }
            }
        }
    }
}