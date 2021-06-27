package org.una.mobile.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.una.mobile.model.Session
import org.una.mobile.ui.screen.LoginScreen
import org.una.mobile.ui.screen.RegisterScreen
import org.una.mobile.ui.screen.UserScreen
import org.una.mobile.ui.screen.home.HomeScreen
import org.una.mobile.viewmodel.FlightViewModel
import org.una.mobile.viewmodel.ScheduleViewModel
import org.una.mobile.viewmodel.SessionViewModel
import org.una.mobile.viewmodel.UserViewModel
import org.una.mobile.viewmodel.form.UserFormViewModel

@ExperimentalAnimationApi
@Composable
fun ApplicationNavigationHost(
    modifier: Modifier = Modifier,
    // Navigation Properties
    navController: NavHostController = rememberNavController(),
    startDestination: ApplicationScreen = ApplicationScreen.Home,
    // ViewModels
    sessionViewModel: SessionViewModel = viewModel(),
    userViewModel: UserViewModel = viewModel(),
    userFormViewModel: UserFormViewModel = viewModel(),
    scheduleViewModel: ScheduleViewModel = viewModel(),
    flightViewModel: FlightViewModel = viewModel(),
) {
    NavHost(navController, startDestination, modifier) {
        composable(ApplicationScreen.Home) {
            HomeScreen(scheduleViewModel = scheduleViewModel, flightViewModel = flightViewModel)
        }
        navigation(route = ApplicationScreen.Session, startDestination = ApplicationScreen.Login) {
            composable(ApplicationScreen.Login) {
                LoginScreen(onNavigateToRegister = { navController.navigate(ApplicationScreen.Register) },
                    onLogin = { username, password -> sessionViewModel.logIn(username, password) })
            }
            composable(ApplicationScreen.Register) {
                RegisterScreen(onReturn = { navController.popBackStack() },
                    onRegister = { username, password, name, lastname, email, address, mobilephone, workphone ->
                        userViewModel.register(username,
                            password,
                            name,
                            lastname,
                            email,
                            address,
                            workphone,
                            mobilephone)
                    })
            }
        }
        composable(ApplicationScreen.User) {
            val session: Session? by sessionViewModel.session.observeAsState()
            val token: String? by remember(session) { derivedStateOf { session?.token } }
            UserScreen(viewModel = userFormViewModel,
                onUpdate = { name, lastname, email, address, mobilephone, workphone ->
                    if (token != null) {
                        userViewModel.update(token!!,
                            name,
                            lastname,
                            email,
                            address,
                            workphone,
                            mobilephone)
                    }
                })
        }
        composable(ApplicationScreen.Purchases) {
            // TODO: PurchasesScreen()
        }
        composable(ApplicationScreen.Reservation) {
            // TODO: ReservationScreen()
        }
    }
}