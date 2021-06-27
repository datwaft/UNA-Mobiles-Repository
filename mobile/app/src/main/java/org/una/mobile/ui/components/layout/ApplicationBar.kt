package org.una.mobile.ui.components.layout

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import org.una.mobile.R
import org.una.mobile.ui.navigation.ApplicationScreen
import org.una.mobile.ui.theme.Theme

@Composable
fun ApplicationBar(
    navController: NavController,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentScreen by remember(backStackEntry) {
        derivedStateOf {
            ApplicationScreen.fromRoute(backStackEntry.value?.destination?.route)
        }
    }

    ApplicationBar(
        title = stringResource(currentScreen.title),
        onMenuClick = onMenuClick,
        modifier = modifier,
    )
}

@Composable
private fun ApplicationBar(
    title: String,
    onMenuClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TopAppBar(
        title = { Text(title) },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(Icons.Filled.Menu, stringResource(R.string.action_drawer_open_description))
            }
        },
        modifier = modifier,
    )
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    Theme {
        ApplicationBar("Title", {}, Modifier.padding(16.dp))
    }
}