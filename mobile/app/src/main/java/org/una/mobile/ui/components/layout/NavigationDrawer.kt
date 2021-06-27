package org.una.mobile.ui.components.layout

import android.util.Log
import androidx.compose.animation.animateColor
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.una.mobile.R
import org.una.mobile.model.User
import org.una.mobile.ui.components.constants.FadeInAnimationDelay
import org.una.mobile.ui.components.constants.FadeInAnimationDuration
import org.una.mobile.ui.components.constants.FadeOutAnimationDuration
import org.una.mobile.ui.navigation.ApplicationScreen
import org.una.mobile.ui.navigation.ApplicationScreen.Home
import org.una.mobile.ui.navigation.ApplicationScreen.Session
import org.una.mobile.ui.navigation.NavigationDrawerMenuItem
import org.una.mobile.ui.navigation.NavigationDrawerMenuItem.Item
import org.una.mobile.ui.navigation.NavigationDrawerMenuItem.Screen
import org.una.mobile.ui.navigation.switch
import org.una.mobile.ui.theme.Theme

@Composable
fun NavigationDrawer(
    currentUser: User?,
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    items: List<NavigationDrawerMenuItem> = emptyList(),
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentScreen: ApplicationScreen by remember(backStackEntry) {
        derivedStateOf {
            ApplicationScreen.fromRoute(backStackEntry.value?.destination?.route)
        }
    }

    NavigationDrawer(
        items.map {
            when (it) {
                is Screen -> Screen(it.screen) { navController.switch(it.screen); it.action?.let { it() } }
                else -> it
            }
        },
        currentUser,
        currentScreen,
        modifier,
    )
}

@Composable
private fun NavigationDrawer(
    items: List<NavigationDrawerMenuItem>,
    currentUser: User?,
    currentScreen: ApplicationScreen,
    modifier: Modifier = Modifier,
) {
    Column(modifier.fillMaxWidth()) {
        NavigationDrawerHeader(when (currentUser) {
            null -> stringResource(R.string.header_session_null)
            else -> stringResource(R.string.header_session_exists, currentUser.fullName)
        })
        LazyColumn(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { item ->
                when (item) {
                    is Screen -> item.let { (item, onPress) ->
                        NavigationDrawerItem(stringResource(item.label),
                            selected = (item == currentScreen || item == currentScreen.parent),
                            onPress = onPress ?: {})
                    }
                    is Item -> item.let { (label, onPress) ->
                        NavigationDrawerItem(label,
                            selected = false,
                            onPress = onPress ?: {})
                    }
                }
            }
        }
    }
}

@Composable
private fun NavigationDrawerHeader(message: String, modifier: Modifier = Modifier) {
    Surface(
        color = MaterialTheme.colors.primarySurface,
        elevation = 4.dp,
        modifier = modifier.fillMaxWidth()
    ) {
        Text(message, style = MaterialTheme.typography.h6,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(top = 64.dp, bottom = 16.dp))
    }
}

@Composable
private fun NavigationDrawerItem(
    label: String,
    selected: Boolean,
    onPress: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val transitionData = updateTransitionData(selected)

    Surface(
        color = transitionData.backgroundColor,
        contentColor = transitionData.contentColor,
        elevation = transitionData.elevation,
        shape = MaterialTheme.shapes.small,
        modifier = modifier,
    ) {
        Text(label,
            style = MaterialTheme.typography.subtitle1,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .clickable { onPress() }
                .padding(8.dp))
    }
}

@Composable
private fun updateTransitionData(state: Boolean): TransitionData {
    val transition = updateTransition(state, "onSelected")
    val colorAnimSpec = remember {
        tween<Color>(
            durationMillis = when {
                state -> FadeInAnimationDuration + FadeInAnimationDelay
                else -> FadeOutAnimationDuration
            },
            easing = LinearEasing,
        )
    }
    val dpAnimSpec = remember {
        tween<Dp>(
            durationMillis = when {
                state -> FadeInAnimationDuration + FadeInAnimationDelay
                else -> FadeOutAnimationDuration
            },
            easing = LinearEasing,
        )
    }
    val contentColor = transition.animateColor(transitionSpec = { colorAnimSpec },
        label = "backgroundColor") {
        when {
            it -> contentColorFor(MaterialTheme.colors.primarySurface)
            else -> contentColorFor(MaterialTheme.colors.background)
        }
    }
    val backgroundColor = transition.animateColor(transitionSpec = { colorAnimSpec },
        label = "backgroundColor") {
        when {
            it -> MaterialTheme.colors.primarySurface
            else -> MaterialTheme.colors.background
        }
    }
    val elevation = transition.animateDp(transitionSpec = { dpAnimSpec },
        label = "elevation") {
        when {
            it -> 8.dp
            else -> 0.dp
        }
    }
    return remember(transition) { TransitionData(contentColor, backgroundColor, elevation) }
}

private class TransitionData(
    contentColor: State<Color>,
    backgroundColor: State<Color>,
    elevation: State<Dp>,
) {
    val contentColor by contentColor
    val backgroundColor by backgroundColor
    val elevation by elevation
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    var currentScreen: ApplicationScreen by remember { mutableStateOf(Home) }

    Theme {
        NavigationDrawer(
            items = listOf(
                Screen(Home) { currentScreen = Home },
                Screen(Session) { currentScreen = Session },
                Item("Logout") { Log.d("PREVIEW", "Emitted logout event") }
            ),
            currentUser = null,
            currentScreen = currentScreen)
    }
}