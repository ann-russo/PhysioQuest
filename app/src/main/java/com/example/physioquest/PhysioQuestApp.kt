package com.example.physioquest

import android.content.res.Resources
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.example.physioquest.ui.theme.PhysioQuestTheme
import com.example.physioquest.common.snackbar.SnackbarManager
import com.example.physioquest.screens.account.AccountRoute
import com.example.physioquest.screens.duellmodus.DuellmodusRoute
import com.example.physioquest.screens.home.HomeScreen
import com.example.physioquest.screens.leaderboard.LeaderboardScreen
import com.example.physioquest.screens.lernmodus.LernmodusRoute
import com.example.physioquest.screens.lernmodus.ResultsScreen
import com.example.physioquest.screens.login.LoginScreen
import com.example.physioquest.screens.registration.RegistrationScreen
import com.example.physioquest.screens.start.StartScreen
import com.example.physioquest.screens.welcome.WelcomeScreen
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import com.google.accompanist.navigation.animation.rememberAnimatedNavController
import kotlinx.coroutines.CoroutineScope

@Composable
@ExperimentalMaterial3Api
@OptIn(ExperimentalAnimationApi::class)
fun PhysioQuestApp() {
    PhysioQuestTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val snackbarHostState = remember { SnackbarHostState() }
            val appState = rememberAppState(snackbarHostState = snackbarHostState)
            Scaffold(
                snackbarHost = {
                    SnackbarHost(
                        hostState = snackbarHostState,
                        modifier = Modifier.padding(8.dp),
                        snackbar = { snackbarData ->
                            Snackbar(
                                snackbarData,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    )
                }
            ) { paddingValues ->
                AnimatedNavHost(
                    navController = appState.navController,
                    startDestination = START_SCREEN,
                    modifier = Modifier.padding(paddingValues)
                ) {
                    physioQuestGraph(appState)
                }
            }
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun rememberAppState(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController = rememberAnimatedNavController(),
    snackbarManager: SnackbarManager = SnackbarManager,
    resources: Resources = resources(),
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) =
    remember(snackbarHostState, navController, snackbarManager, resources, coroutineScope) {
        PhysioQuestAppState(
            snackbarHostState,
            navController,
            snackbarManager,
            resources,
            coroutineScope
        )
    }

@Composable
@ReadOnlyComposable
fun resources(): Resources {
    LocalConfiguration.current
    return LocalContext.current.resources
}

@OptIn(ExperimentalAnimationApi::class)
fun NavGraphBuilder.physioQuestGraph(appState: PhysioQuestAppState) {
    composable(START_SCREEN) {
        StartScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }
    
    composable(WELCOME_SCREEN) {
        WelcomeScreen(
            openAndPopUp = { route, popUp ->
                appState.navigateAndPopUp(route, popUp)
            }
        )
    }

    composable(
        route = LOGIN_SCREEN,
        enterTransition = {
            when (initialState.destination.route) {
                WELCOME_SCREEN ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                WELCOME_SCREEN ->
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        },
        popEnterTransition = {
            when (initialState.destination.route) {
                WELCOME_SCREEN ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        },
        popExitTransition = {
            when (targetState.destination.route) {
                WELCOME_SCREEN ->
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        }
    ) {
        LoginScreen(
            openScreen = { route -> appState.navigate(route) },
            openAndPopUp = { route, popUp ->
                appState.navigateAndPopUp(route, popUp)
            }
        )
    }

    composable(
        route = REGISTRATION_SCREEN,
        enterTransition = {
            when (initialState.destination.route) {
                WELCOME_SCREEN ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                WELCOME_SCREEN ->
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        },
        popEnterTransition = {
            when (initialState.destination.route) {
                WELCOME_SCREEN ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        },
        popExitTransition = {
            when (targetState.destination.route) {
                WELCOME_SCREEN ->
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        }
    ) {
        RegistrationScreen(
            openScreen = { route -> appState.navigate(route) },
            openAndPopUp = { route, popUp ->
                appState.navigateAndPopUp(route, popUp)
            }
        )
    }

    composable(HOME_SCREEN) {
        HomeScreen(
            restartApp = { route -> appState.clearAndNavigate(route) },
            openScreen = { route -> appState.navigate(route) }
        )
    }

    composable(
        route = LERNMODUS_ROUTE,
        enterTransition = {
            when (initialState.destination.route) {
                HOME_SCREEN ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(700)
                    )

                else -> null
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                HOME_SCREEN ->
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(700)
                    )

                else -> null
            }
        },
        popEnterTransition = {
            when (initialState.destination.route) {
                HOME_SCREEN ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(700)
                    )

                else -> null
            }
        },
        popExitTransition = {
            when (targetState.destination.route) {
                HOME_SCREEN ->
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(700)
                    )

                else -> null
            }
        }
    ) {
        LernmodusRoute(
            onQuizComplete = { appState.navigate("ResultsScreen/$it") },
            openScreen = { route -> appState.navigate(route) },
            onNavUp = { appState.popUp() },
        )
    }

    composable(
        route = LERNMODUS_RESULTS,
        enterTransition = {
            when (initialState.destination.route) {
                LERNMODUS_ROUTE ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(700)
                    )

                else -> null
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                HOME_SCREEN ->
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(700)
                    )

                else -> null
            }
        },
        popEnterTransition = {
            when (initialState.destination.route) {
                LERNMODUS_ROUTE ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(700)
                    )

                else -> null
            }
        },
        popExitTransition = {
            when (targetState.destination.route) {
                HOME_SCREEN ->
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(700)
                    )

                else -> null
            }
        }
    ) {
        val result = it.arguments?.getString("result")?.toDoubleOrNull()
        ResultsScreen(
            result = result ?: 0.0,
            openScreen = { route -> appState.navigate(route) }
        )
    }

    composable(LEADERBOARD_SCREEN) {
        LeaderboardScreen(
            restartApp = { route -> appState.clearAndNavigate(route) },
            openScreen = { route -> appState.navigate(route) }
        )
    }

    composable(
        route = DUELLMODUS_ROUTE,
        enterTransition = {
            when (initialState.destination.route) {
                HOME_SCREEN ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(700)
                    )

                else -> null
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                HOME_SCREEN ->
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(700)
                    )

                else -> null
            }
        },
        popEnterTransition = {
            when (initialState.destination.route) {
                HOME_SCREEN ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(700)
                    )

                else -> null
            }
        },
        popExitTransition = {
            when (targetState.destination.route) {
                HOME_SCREEN ->
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(700)
                    )

                else -> null
            }
        }
    ) {
        DuellmodusRoute(
            openScreen = { route -> appState.navigate(route) },
            onNavUp = { appState.popUp() }
        )
    }

    composable(ACCOUNT_ROUTE) {
        AccountRoute(
            restartApp = { route -> appState.clearAndNavigate(route) },
            openScreen = { route -> appState.navigate(route) }
        )
    }
}