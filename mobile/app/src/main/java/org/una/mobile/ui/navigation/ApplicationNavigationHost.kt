package org.una.mobile.ui.navigation

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import org.una.mobile.model.Purchase
import org.una.mobile.model.Session
import org.una.mobile.ui.screen.*
import org.una.mobile.ui.screen.home.HomeScreen
import org.una.mobile.viewmodel.*
import org.una.mobile.viewmodel.form.ReservationFormViewModel
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
    purchaseViewModel: PurchaseViewModel = viewModel(),
    ticketViewModel: TicketViewModel = viewModel(),
    reservationFormViewModel: ReservationFormViewModel = viewModel(),
) {
    NavHost(navController, startDestination, modifier) {
        composable(ApplicationScreen.Home) {
            HomeScreen(scheduleViewModel = scheduleViewModel,
                flightViewModel = flightViewModel,
                sessionViewModel = sessionViewModel,
                purchaseViewModel = purchaseViewModel)
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
            LaunchedEffect(true) {
                purchaseViewModel.viewAll()
            }
            val items: List<Purchase> by purchaseViewModel.items.observeAsState(emptyList())
            PurchaseScreen(items, onNavigateToReservationScreen = { purchase, flight ->
                reservationFormViewModel.purchase = purchase
                if (purchase.hasBeenReserved) {
                    ticketViewModel.viewAllPerPurchase(purchase.identifier)
                }
                ticketViewModel.viewAllPerFlight(flight)
                navController.navigate(ApplicationScreen.Reservation)
            })
        }
        composable(ApplicationScreen.Reservation) {
            ReservationScreen(
                onReturn = { navController.popBackStack() },
                reservationFormViewModel = reservationFormViewModel,
                ticketViewModel = ticketViewModel,
            )
        }
    }
}