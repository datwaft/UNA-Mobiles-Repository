package org.una.mobile.ui.screen.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.una.mobile.model.Schedule
import org.una.mobile.ui.components.ListItem
import org.una.mobile.ui.components.layout.SearchBar
import org.una.mobile.ui.theme.Theme

@Composable
fun ScheduleScreen(
    items: List<Schedule>,
    modifier: Modifier = Modifier,
) {
    var query: String by remember { mutableStateOf("") }
    val filteredItems: List<Schedule> by remember(items, query) {
        derivedStateOf {
            items.filter { item ->
                query.split(" ").filter(String::isNotBlank).all { item.contains(it) }
            }
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = { SearchBar(query, { query = it }, Modifier.padding(16.dp)) },
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(filteredItems, key = { it.identifier }) { item ->
                ScheduleScreenItem(item)
            }
        }
    }
}

@Composable
private fun ScheduleScreenItem(item: Schedule, modifier: Modifier = Modifier) {
    ListItem(modifier) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            item.apply {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(route, fontWeight = FontWeight.SemiBold)
                    Text(formattedDepartureTime, style = MaterialTheme.typography.body2)
                }
                Column(verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.End) {
                    Text(formattedPrice, style = MaterialTheme.typography.body2)
                    Text(formattedDuration, style = MaterialTheme.typography.body2)
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    val items: List<Schedule> by remember {
        mutableStateOf(listOf(
            Schedule(
                identifier = 0,
                origin = "Costa Rica",
                destination = "China",
                rawDepartureTime = "22:30",
                weekday = "Sunday",
                rawDuration = "10:00",
                price = 2500.00,
                discount = 0.30,
            ),
            Schedule(
                identifier = 1,
                origin = "China",
                destination = "Costa Rica",
                rawDepartureTime = "00:30",
                weekday = "Sunday",
                rawDuration = "08:00",
                price = 3700.00,
                discount = 0.50,
            ),
            Schedule(
                identifier = 2,
                origin = "Costa Rica",
                destination = "Atlantis",
                rawDepartureTime = "00:00",
                weekday = "Sunday",
                rawDuration = "23:59",
                price = 7000.00,
                discount = 0.25,
            ),
        ))
    }

    Theme {
        Surface(color = MaterialTheme.colors.background) {
            ScheduleScreen(items)
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ScheduleScreenItemPreview() {
    val item = Schedule(
        identifier = 0,
        origin = "Costa Rica",
        destination = "China",
        rawDepartureTime = "22:30",
        weekday = "Sunday",
        rawDuration = "10:00",
        price = 2500.00,
        discount = 0.30,
    )

    Theme {
        ScheduleScreenItem(item, Modifier.padding(16.dp))
    }
}