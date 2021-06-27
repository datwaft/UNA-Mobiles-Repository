package org.una.mobile.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.una.mobile.ui.navigation.HomeScreen.Flights
import org.una.mobile.ui.navigation.HomeScreen.Schedules

@ExperimentalAnimationApi
@Composable
fun HomeNavigationHost(
    modifier: Modifier = Modifier,
    // Navigation Properties
    navController: NavHostController = rememberNavController(),
    startDestination: HomeScreen = Flights,
) {
    NavHost(navController, startDestination, modifier) {
        composable(Flights) {
            // TODO: FlightsScreen()
        }
        composable(Schedules) {
            // TODO: SchedulesScreen()
        }
    }
}