package org.una.mobile.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.una.mobile.model.Purchase
import org.una.mobile.ui.components.ListItem
import org.una.mobile.ui.components.layout.SearchBar
import org.una.mobile.ui.theme.Theme

@Composable
fun PurchaseScreen(
    items: List<Purchase>,
    onNavigateToReservationScreen: (purchase: Purchase, flight: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var query: String by remember { mutableStateOf("") }
    val filteredItems: List<Purchase> by remember(items, query) {
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
                PurchaseScreenItem(item,
                    onLongPress = {
                        onNavigateToReservationScreen(item, item.flight)
                    })
            }
        }
    }
}

@Composable
private fun PurchaseScreenItem(
    item: Purchase,
    onLongPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ListItem(onLongPress, modifier) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            item.apply {
                Row(Modifier.weight(1f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically) {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(route, fontWeight = FontWeight.SemiBold)
                        Text(formattedDate, style = MaterialTheme.typography.body2)
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp),
                        horizontalAlignment = Alignment.End) {
                        Text(formattedTicketAmount, style = MaterialTheme.typography.body2)
                        Text(formattedTotalCost, style = MaterialTheme.typography.body2)
                    }
                }
                Column(verticalArrangement = Arrangement.Center) {
                    Icon(when {
                        hasBeenReserved -> Icons.Filled.Visibility
                        else -> Icons.Filled.ShoppingCart
                    }, null, Modifier.padding(start = 16.dp))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    val item = Purchase(
        0,
        "2021-06-27T18:01:00",
        "Costa Rica",
        "Panama",
        "2021-06-27",
        "2021-06-30",
        3,
        4500.0,
        true,
        30,
        9,
        1,
    )

    Theme {
        PurchaseScreenItem(item, {}, Modifier.padding(16.dp))
    }
}