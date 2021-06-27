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
enum class ApplicationScreen(
    @StringRes val title: Int = R.string.screen_default_name,
    @StringRes val label: Int = title,
    val parent: ApplicationScreen? = null,
) {
    Home(title = R.string.screen_home_name),

    Session(label = R.string.screen_session_login_name),
    Login(title = R.string.screen_session_login_name, parent = Session),
    Register(title = R.string.screen_session_register_name, parent = Session),

    User(title = R.string.screen_user_name),
    Purchases(title = R.string.screen_purchases_name),
    Reservation(title = R.string.screen_reservation_name);

    companion object {
        fun fromRoute(route: String?): ApplicationScreen =
            when (route?.substringBefore("/")) {
                Home.name -> Home

                Session.name -> Session
                Login.name -> Login
                Register.name -> Register

                User.name -> User
                Purchases.name -> Purchases
                Reservation.name -> Reservation

                null -> Home
                else -> throw IllegalArgumentException("Route $route is not recognized.")
            }
    }
}

@Composable
internal fun NavHost(
    navController: NavHostController,
    startDestination: ApplicationScreen,
    modifier: Modifier = Modifier,
    route: String? = null,
    builder: NavGraphBuilder.() -> Unit,
) {
    NavHost(navController, startDestination.name, modifier, route, builder)
}

internal inline fun NavGraphBuilder.navigation(
    startDestination: ApplicationScreen,
    route: ApplicationScreen,
    builder: NavGraphBuilder.() -> Unit,
) {
    navigation(startDestination.name, route.name, builder)
}

internal fun NavGraphBuilder.composable(
    route: ApplicationScreen,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    content: @Composable (NavBackStackEntry) -> Unit,
) {
    composable(route.name, arguments, deepLinks, content)
}

internal fun NavController.switch(screen: ApplicationScreen) {
    val currentApplicationScreen =
        ApplicationScreen.fromRoute(this.currentBackStackEntry?.destination?.route)
    this.backQueue.removeIf { ApplicationScreen.fromRoute(it.destination.route) != currentApplicationScreen }
    this.navigate(screen) {
        popUpTo(currentApplicationScreen) {
            inclusive = true
        }
        launchSingleTop = true
    }
}

internal fun NavController.navigate(screen: ApplicationScreen) {
    this.navigate(screen.name)
}

internal fun NavController.navigate(
    screen: ApplicationScreen,
    builder: NavOptionsBuilder.() -> Unit,
) {
    this.navigate(screen.name, builder)
}

internal fun NavOptionsBuilder.popUpTo(screen: ApplicationScreen) {
    this.popUpTo(screen.name)
}

internal fun NavOptionsBuilder.popUpTo(
    screen: ApplicationScreen,
    popUpToBuilder: PopUpToBuilder.() -> Unit,
) {
    this.popUpTo(screen.name, popUpToBuilder)
}