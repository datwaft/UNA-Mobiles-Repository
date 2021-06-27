package org.una.mobile.ui.navigation

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.navigation.*
import androidx.navigation.compose.NamedNavArgument
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import org.una.mobile.R

@Immutable
enum class HomeScreen(
    @StringRes val title: Int = R.string.screen_default_name,
    @StringRes val label: Int = title,
    val parent: HomeScreen? = null,
) {
    Flights(title = R.string.screen_home_flights_name),
    Schedules(title = R.string.screen_home_schedules_name);

    companion object {
        fun fromRoute(route: String?): HomeScreen =
            when (route?.substringBefore("/")) {
                Flights.name -> Flights
                Schedules.name -> Schedules

                null -> Flights
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
}

@Composable
internal fun NavHost(
    navController: NavHostController,
    startDestination: HomeScreen,
    modifier: Modifier = Modifier,
    route: String? = null,
    builder: NavGraphBuilder.() -> Unit,
) {
    NavHost(navController, startDestination.name, modifier, route, builder)
}

internal inline fun NavGraphBuilder.navigation(
    startDestination: HomeScreen,
    route: HomeScreen,
    builder: NavGraphBuilder.() -> Unit,
) {
    navigation(startDestination.name, route.name, builder)
}

internal fun NavGraphBuilder.composable(
    route: HomeScreen,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit,
) {
    composable(route.name, arguments, deepLinks, content)
}

internal fun NavController.switch(screen: HomeScreen) {
    val currentHomeScreen =
        HomeScreen.fromRoute(this.currentBackStackEntry?.destination?.route)
    this.backQueue.removeIf { HomeScreen.fromRoute(it.destination.route) != currentHomeScreen }
    this.navigate(screen) {
        popUpTo(currentHomeScreen) {
            inclusive = true
        }
        launchSingleTop = true
    }
}

internal fun NavController.navigate(screen: HomeScreen) {
    this.navigate(screen.name)
}

internal fun NavController.navigate(
    screen: HomeScreen,
    builder: NavOptionsBuilder.() -> Unit,
) {
    this.navigate(screen.name, builder)
}

internal fun NavOptionsBuilder.popUpTo(screen: HomeScreen) {
    this.popUpTo(screen.name)
}

internal fun NavOptionsBuilder.popUpTo(
    screen: HomeScreen,
    popUpToBuilder: PopUpToBuilder.() -> Unit,
) {
    this.popUpTo(screen.name, popUpToBuilder)
}