package org.una.mobile.ui.screen.home

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import org.una.mobile.ui.components.layout.home.HomeTabRow
import org.una.mobile.ui.navigation.HomeNavigationHost
import org.una.mobile.ui.navigation.HomeScreen.Flights
import org.una.mobile.ui.navigation.HomeScreen.Schedules
import org.una.mobile.viewmodel.FlightViewModel
import org.una.mobile.viewmodel.PurchaseViewModel
import org.una.mobile.viewmodel.ScheduleViewModel
import org.una.mobile.viewmodel.SessionViewModel

@ExperimentalAnimationApi
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    // ViewModels
    sessionViewModel: SessionViewModel = viewModel(),
    scheduleViewModel: ScheduleViewModel = viewModel(),
    flightViewModel: FlightViewModel = viewModel(),
    purchaseViewModel: PurchaseViewModel = viewModel(),
) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            HomeTabRow(navController = navController, items = listOf(Flights, Schedules))
        },
        modifier = modifier,
    ) {
        HomeNavigationHost(
            navController = navController,
            modifier = Modifier.padding(it),
            scheduleViewModel = scheduleViewModel,
            flightViewModel = flightViewModel,
            sessionViewModel = sessionViewModel,
            purchaseViewModel = purchaseViewModel,
        )
    }
}