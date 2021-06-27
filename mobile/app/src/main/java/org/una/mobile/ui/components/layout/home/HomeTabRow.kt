package org.una.mobile.ui.components.layout.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.*
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.una.mobile.ui.components.constants.FadeInAnimationDelay
import org.una.mobile.ui.components.constants.FadeInAnimationDuration
import org.una.mobile.ui.components.constants.FadeOutAnimationDuration
import org.una.mobile.ui.navigation.HomeScreen
import org.una.mobile.ui.navigation.switch
import org.una.mobile.ui.theme.Theme
import java.util.*

@Composable
fun HomeTabRow(
    modifier: Modifier = Modifier,
    navController: NavController = rememberNavController(),
    items: List<HomeScreen> = emptyList(),
) {
    val backStackEntry = navController.currentBackStackEntryAsState()
    val currentScreen: HomeScreen by remember(backStackEntry) {
        derivedStateOf {
            HomeScreen.fromRoute(backStackEntry.value?.destination?.route)
        }
    }

    HomeTabRow(items.map { it to { navController.switch(it) } }, currentScreen, modifier)
}

@Composable
private fun HomeTabRow(
    items: List<Pair<HomeScreen, () -> Unit>>,
    currentScreen: HomeScreen,
    modifier: Modifier = Modifier,
) {
    Surface(
        color = getColors().TabBackgroundColor,
        elevation = 2.dp,
        modifier = modifier
            .height(TabHeight)
            .fillMaxWidth()
    ) {
        Row(Modifier
            .selectableGroup()) {
            items.forEach { (item, onSelected) ->
                Tab(stringResource(item.label),
                    selected = currentScreen == item,
                    onSelected = onSelected)
            }
        }
    }
}

@Composable
private fun Tab(
    label: String,
    selected: Boolean,
    onSelected: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val durationMillis = when {
        selected -> FadeInAnimationDuration
        else -> FadeOutAnimationDuration
    }
    val animSpec = remember {
        tween<Color>(
            durationMillis = durationMillis,
            easing = LinearEasing,
            delayMillis = FadeInAnimationDelay,
        )
    }
    val color by animateColorAsState(
        targetValue = when {
            selected -> getColors().TabContentColor
            else -> getColors().TabContentColor.copy(alpha = InactiveTabOpacity)
        },
        animationSpec = animSpec
    )

    Row(
        modifier = modifier
            .animateContentSize()
            .selectable(
                selected = selected,
                role = Role.Tab,
                interactionSource = remember { MutableInteractionSource() },
                indication = rememberRipple(
                    bounded = false,
                    radius = Dp.Unspecified,
                    color = Color.Unspecified,
                ),
                onClick = onSelected
            )
            .height(TabHeight)
            .padding(16.dp)
    ) {
        Text(label.uppercase(Locale.ROOT), color = color)
    }
}

@Preview(showBackground = true)
@Composable
private fun Preview() {
    var currentScreen: HomeScreen by remember { mutableStateOf(HomeScreen.Flights) }

    Theme {
        HomeTabRow(items = listOf(
            HomeScreen.Flights to { currentScreen = HomeScreen.Flights },
            HomeScreen.Schedules to { currentScreen = HomeScreen.Schedules },
        ), currentScreen = currentScreen)
    }
}

@Composable
private fun getColors() =
    object {
        val TabBackgroundColor = MaterialTheme.colors.primarySurface
        val TabContentColor = contentColorFor(TabBackgroundColor)
    }

private val TabHeight = 56.dp
private const val InactiveTabOpacity = 0.60f