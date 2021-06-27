package org.una.mobile.ui.navigation

import androidx.compose.material.ScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.una.mobile.R
import org.una.mobile.ui.navigation.ApplicationScreen.*
import org.una.mobile.ui.navigation.NavigationDrawerMenuItem.Item
import org.una.mobile.ui.navigation.NavigationDrawerMenuItem.Screen
import org.una.mobile.viewmodel.SessionViewModel
import org.una.mobile.viewmodel.form.UserFormViewModel

sealed class NavigationDrawerMenuItem(open val action: (() -> Unit)? = null) {
    data class Screen(val screen: ApplicationScreen, override val action: (() -> Unit)? = null) :
        NavigationDrawerMenuItem(action)

    data class Item(val item: String, override val action: (() -> Unit)? = null) :
        NavigationDrawerMenuItem(action)
}

@Composable
fun buildNavigationDrawerMenuItems(
    isLoggedIn: Boolean,
    user: org.una.mobile.model.User?,
    scaffoldState: ScaffoldState,
    navController: NavController = rememberNavController(),
    // ViewModels
    sessionViewModel: SessionViewModel = viewModel(),
    userFormViewModel: UserFormViewModel = viewModel(),
) =
    rememberCoroutineScope().let { coroutineScope ->
        when {
            isLoggedIn -> listOf(
                Screen(Home) {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
                Screen(User) {
                    userFormViewModel.import(user!!)
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
                Screen(Purchases) {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
                Item(stringResource(R.string.action_logout_label)) {
                    sessionViewModel.logOut()
                    navController.switch(Home)
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                }
            )
            else -> listOf(
                Screen(Home) {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
                Screen(Session) {
                    coroutineScope.launch {
                        scaffoldState.drawerState.close()
                    }
                },
            )
        }
    }