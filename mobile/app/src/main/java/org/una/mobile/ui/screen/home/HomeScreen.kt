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
import org.una.mobile.viewmodel.ScheduleViewModel

@ExperimentalAnimationApi
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    // ViewModels
    scheduleViewModel: ScheduleViewModel = viewModel(),
) {
    val navController = rememberNavController()

    Scaffold(
        topBar = {
            HomeTabRow(navController = navController, items = listOf(Flights, Schedules))
        },
        modifier = modifier,
    ) {
        HomeNavigationHost(navController = navController,
            modifier = Modifier.padding(it),
            scheduleViewModel = scheduleViewModel)
    }
}