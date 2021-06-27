package org.una.mobile.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.una.mobile.model.Flight
import org.una.mobile.model.Schedule
import org.una.mobile.ui.navigation.HomeScreen.Flights
import org.una.mobile.ui.navigation.HomeScreen.Schedules
import org.una.mobile.ui.screen.home.FlightScreen
import org.una.mobile.ui.screen.home.ScheduleScreen
import org.una.mobile.viewmodel.FlightViewModel
import org.una.mobile.viewmodel.ScheduleViewModel

@ExperimentalAnimationApi
@Composable
fun HomeNavigationHost(
    modifier: Modifier = Modifier,
    // Navigation Properties
    navController: NavHostController = rememberNavController(),
    startDestination: HomeScreen = Flights,
    // ViewModels
    scheduleViewModel: ScheduleViewModel = viewModel(),
    flightViewModel: FlightViewModel = viewModel(),
) {
    NavHost(navController, startDestination, modifier) {
        composable(Flights) {
            LaunchedEffect(true) {
                flightViewModel.viewAllWithDiscount()
            }
            val items: List<Flight> by flightViewModel.items.observeAsState(emptyList())
            FlightScreen(items)
        }
        composable(Schedules) {
            LaunchedEffect(true) {
                scheduleViewModel.viewAllWithDiscount()
            }
            val items: List<Schedule> by scheduleViewModel.items.observeAsState(emptyList())
            ScheduleScreen(items)
        }
    }
}