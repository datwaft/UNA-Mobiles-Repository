package org.una.mobile.ui.screen.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.una.mobile.R
import org.una.mobile.model.Flight
import org.una.mobile.ui.components.Dropdown
import org.una.mobile.ui.components.ListItem
import org.una.mobile.ui.components.layout.SearchBar
import org.una.mobile.ui.theme.Theme

@ExperimentalAnimationApi
@Composable
fun FlightScreen(
    items: List<Flight>,
    modifier: Modifier = Modifier,
) {
    var query: String by remember { mutableStateOf("") }
    var selectedOrigin: String? by remember { mutableStateOf(null) }
    val originSet: Set<String> by remember(items) {
        derivedStateOf {
            items.map { it.origin }.toSet()
        }
    }
    var selectedDestination: String? by remember { mutableStateOf(null) }
    val destinationSet: Set<String> by remember(items) {
        derivedStateOf {
            items.map { it.destination }.toSet()
        }
    }
    val filteredItems: List<Flight> by remember(items, query) {
        derivedStateOf {
            items.filter { item ->
                query.split(" ").filter(String::isNotBlank).all { item.contains(it) } &&
                        (selectedOrigin == null || item.origin == selectedOrigin) &&
                        (selectedDestination == null || item.destination == selectedDestination)
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            Column(Modifier
                .padding(16.dp)
                .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SearchBar(query, { query = it })
                Row(Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Box(Modifier.weight(1f)) {
                        Dropdown(stringResource(R.string.field_origin_label),
                            originSet.toList(),
                            stringResource(R.string.field_origin_placeholder),
                            selectedOrigin, { selectedOrigin = it },
                            Modifier.fillMaxWidth(),
                            cleanable = true)
                    }
                    Box(Modifier.weight(1f)) {
                        Dropdown(stringResource(R.string.field_destination_label),
                            destinationSet.toList(),
                            stringResource(R.string.field_destination_placeholder),
                            selectedDestination, { selectedDestination = it },
                            Modifier.fillMaxWidth(),
                            cleanable = true)
                    }
                }
            }
        },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredItems, key = { it.identifier }) { item ->
                FlightScreenItem(item)
            }
        }
    }
}

@Composable
private fun FlightScreenItem(
    item: Flight,
    modifier: Modifier = Modifier,
    onLongPress: (() -> Unit)? = null,
) {
    val content: @Composable BoxScope.() -> Unit = {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            item.apply {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(route, fontWeight = FontWeight.SemiBold)
                    Text(formattedDate, style = MaterialTheme.typography.body2)
                }
                Column(verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.End) {
                    Text(formattedPrice, style = MaterialTheme.typography.body2)
                    Text(formattedPassengers, style = MaterialTheme.typography.body2)
                }
            }
        }
    }
    if (onLongPress == null) {
        ListItem(modifier, content)
    } else {
        ListItem(onLongPress = onLongPress, modifier = modifier, content = content)
    }
}

@ExperimentalAnimationApi
@Preview
@Composable
private fun FlightScreenPreview() {
    val items: List<Flight> by remember {
        mutableStateOf(listOf(
            Flight(
                0,
                "Costa Rica",
                "China",
                "2020-12-30",
                null,
                12,
                120,
                1800.00,
            ),
            Flight(
                1,
                "Costa Rica",
                "China",
                "2021-02-28",
                "2021-03-24",
                0,
                120,
                2300.00,
            ),
            Flight(
                2,
                "Costa Rica",
                "Atlantis",
                "2020-12-30",
                null,
                24,
                120,
                2800.00,
            ),
        ))
    }

    Theme {
        Surface(color = MaterialTheme.colors.background) {
            FlightScreen(items)
        }
    }
}

@Preview
@Composable
private fun FlightScreenItemPreview() {
    val item = Flight(
        0,
        "Costa Rica",
        "China",
        "2020-12-30",
        null,
        12,
        120,
        1800.00,
    )

    Theme {
        Surface(color = MaterialTheme.colors.background) {
            FlightScreenItem(item, Modifier.padding(16.dp))
        }
    }
}