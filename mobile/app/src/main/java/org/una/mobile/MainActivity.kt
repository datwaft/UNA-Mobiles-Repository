package org.una.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.una.mobile.client.ScheduleWebSocketClient
import org.una.mobile.client.SessionWebSocketClient
import org.una.mobile.client.UserWebSocketClient
import org.una.mobile.model.Session
import org.una.mobile.model.User
import org.una.mobile.ui.components.layout.ApplicationBar
import org.una.mobile.ui.components.layout.NavigationDrawer
import org.una.mobile.ui.layout.snackbarHost
import org.una.mobile.ui.navigation.ApplicationNavigationHost
import org.una.mobile.ui.navigation.ApplicationScreen.Home
import org.una.mobile.ui.navigation.buildNavigationDrawerMenuItems
import org.una.mobile.ui.navigation.switch
import org.una.mobile.ui.theme.Theme
import org.una.mobile.viewmodel.SessionListener
import org.una.mobile.viewmodel.SessionViewModel
import org.una.mobile.viewmodel.UserListener
import org.una.mobile.viewmodel.UserViewModel
import org.una.mobile.viewmodel.form.UserFormViewModel

@ExperimentalAnimationApi
class MainActivity : ComponentActivity() {
    override fun onResume() {
        super.onResume()
        SessionWebSocketClient.connect(lifecycleScope)
        UserWebSocketClient.connect(lifecycleScope)
        ScheduleWebSocketClient.connect(lifecycleScope)
    }

    override fun onPause() {
        super.onPause()
        SessionWebSocketClient.close()
        UserWebSocketClient.close()
        ScheduleWebSocketClient.close()
        lifecycleScope.launch {
            SessionViewModel.internalChannel.send(SessionViewModel.InternalEvent.Clear)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Theme {
                Surface(color = MaterialTheme.colors.background) {
                    App(Modifier.animateContentSize())
                }
            }
        }
    }
}

@ExperimentalAnimationApi
@Composable
private fun App(
    modifier: Modifier = Modifier,
) {
    /***************
     * Compose State
     */
    val navController = rememberNavController()
    val scaffoldState = rememberScaffoldState()
    val coroutineScope = rememberCoroutineScope()

    /************
     * ViewModels
     */
    val sessionViewModel: SessionViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()
    val userFormViewModel: UserFormViewModel = viewModel()

    /************
     * Data State
     */
    val session: Session? by sessionViewModel.session.observeAsState()
    val isLoggedIn: Boolean by sessionViewModel.isLoggedIn.observeAsState(false)
    val user: User? by userViewModel.user.observeAsState()

    /*****************
     * Event Listeners
     */
    SessionListener(scaffoldState)
    UserListener(scaffoldState, navController)

    LaunchedEffect(session) {
        navController.switch(Home)
    }

    LaunchedEffect(user) {
        if (user != null) {
            userFormViewModel.import(user!!)
        }
    }

    /*********
     * Content
     */
    Scaffold(modifier, scaffoldState,
        topBar = {
            ApplicationBar(navController,
                onMenuClick = { coroutineScope.launch { scaffoldState.drawerState.open() } })
        },
        drawerContent = {
            NavigationDrawer(
                currentUser = user,
                navController = navController,
                items = buildNavigationDrawerMenuItems(isLoggedIn, user,
                    scaffoldState,
                    navController,
                    sessionViewModel,
                    userFormViewModel))
        },
        snackbarHost = snackbarHost
    ) {
        ApplicationNavigationHost(navController = navController, modifier = Modifier.padding(it),
            sessionViewModel = sessionViewModel,
            userViewModel = userViewModel,
            userFormViewModel = userFormViewModel)
    }
}