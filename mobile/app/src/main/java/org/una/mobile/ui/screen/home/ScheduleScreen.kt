package org.una.mobile.ui.screen.home

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.una.mobile.model.Flight
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
        topBar = { SearchBar(query, { query = it }) },
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
                0,
                "Costa Rica",
                "China",
                "22:30",
                "Sunday",
                "10:00",
                2500.00,
                0.30,
            ),
            Schedule(
                1,
                "China",
                "Costa Rica",
                "00:30",
                "Sunday",
                "08:00",
                3700.00,
                0.50,
            ),
            Schedule(
                2,
                "Costa Rica",
                "Atlantis",
                "00:00",
                "Sunday",
                "23:59",
                7000.00,
                0.25,
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
        0,
        "Costa Rica",
        "China",
        "22:30",
        "Sunday",
        "10:00",
        2500.00,
        0.30,
    )

    Theme {
        ScheduleScreenItem(item, Modifier.padding(16.dp))
    }
}