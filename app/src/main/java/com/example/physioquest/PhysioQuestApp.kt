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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.example.physioquest.common.snackbar.SnackbarManager
import com.example.physioquest.model.QuizResult
import com.example.physioquest.screens.account.AccountRoute
import com.example.physioquest.screens.home.HomeScreen
import com.example.physioquest.screens.leaderboard.LeaderboardScreen
import com.example.physioquest.screens.login.LoginScreen
import com.example.physioquest.screens.quiz.duellmodus.DuellmodusRoute
import com.example.physioquest.screens.quiz.duelresult.DuelResultsScreen
import com.example.physioquest.screens.quiz.lernmodus.LernmodusRoute
import com.example.physioquest.screens.quiz.lernmodus.ResultsScreen
import com.example.physioquest.screens.registration.RegistrationScreen
import com.example.physioquest.screens.start.StartScreen
import com.example.physioquest.screens.welcome.WelcomeScreen
import com.example.physioquest.ui.theme.PhysioQuestTheme
import com.google.accompanist.navigation.animation.AnimatedNavHost
import com.google.accompanist.navigation.animation.composable
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@Composable
@ExperimentalMaterial3Api
@OptIn(ExperimentalAnimationApi::class)
fun PhysioQuestApp(navController: NavHostController, duelIdFromNotification: MutableState<String?>) {
    PhysioQuestTheme(navigationBarColor = null) {
        Surface(color = MaterialTheme.colorScheme.background) {
            val snackbarHostState = remember { SnackbarHostState() }
            val appState = rememberAppState(
                snackbarHostState = snackbarHostState,
                navController = navController
            )

            LaunchedEffect(duelIdFromNotification.value) {
                duelIdFromNotification.value?.let { duelId ->
                    appState.navController.navigate("DuelResultsScreen/$duelId")
                }
            }

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

@Composable
fun rememberAppState(
    snackbarHostState: SnackbarHostState,
    navController: NavHostController,
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
private fun NavGraphBuilder.startScreen(appState: PhysioQuestAppState) {
    composable(START_SCREEN) {
        StartScreen(openAndPopUp = { route, popUp -> appState.navigateAndPopUp(route, popUp) })
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.welcomeScreen(appState: PhysioQuestAppState) {
    composable(WELCOME_SCREEN) {
        WelcomeScreen(
            openAndPopUp = { route, popUp ->
                appState.navigateAndPopUp(route, popUp)
            }
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.loginScreen(appState: PhysioQuestAppState) {
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
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.registrationScreen(appState: PhysioQuestAppState) {
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
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.homeScreen(appState: PhysioQuestAppState) {
    composable(HOME_SCREEN) {
        HomeScreen(
            restartApp = { route -> appState.clearAndNavigate(route) },
            openScreen = { route -> appState.navigate(route) }
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.lernmodusScreen(appState: PhysioQuestAppState) {
    composable(
        route = LERNMODUS_ROUTE,
        enterTransition = {
            when (initialState.destination.route) {
                HOME_SCREEN ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                HOME_SCREEN ->
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        },
        popEnterTransition = {
            when (initialState.destination.route) {
                HOME_SCREEN ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        },
        popExitTransition = {
            when (targetState.destination.route) {
                HOME_SCREEN ->
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        }
    ) {
        LernmodusRoute(
            onQuizComplete = { result ->
                val jsonResult = Json.encodeToString(result)
                appState
                    .navigate(
                        "ResultsScreen/$jsonResult")
            },
            openScreen = { route -> appState.navigate(route) },
            onNavUp = { appState.popUp() },
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.lernmodusResultsScreen(appState: PhysioQuestAppState) {
    composable(
        route = LERNMODUS_RESULTS,
        enterTransition = {
            when (initialState.destination.route) {
                LERNMODUS_ROUTE ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                HOME_SCREEN ->
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        },
        popEnterTransition = {
            when (initialState.destination.route) {
                LERNMODUS_ROUTE ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        },
        popExitTransition = {
            when (targetState.destination.route) {
                HOME_SCREEN ->
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        }
    ) {
        val jsonString = it.arguments?.getString("result")
        val result = jsonString?.let { json ->
            Json.decodeFromString<QuizResult>(json)
        } ?: QuizResult()

        ResultsScreen(
            result = result,
            openScreen = { route -> appState.navigate(route) }
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.duellmodusScreen(appState: PhysioQuestAppState) {
    composable(
        route = DUELLMODUS_ROUTE,
        enterTransition = {
            when (initialState.destination.route) {
                HOME_SCREEN ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                HOME_SCREEN ->
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        },
        popEnterTransition = {
            when (initialState.destination.route) {
                HOME_SCREEN ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        },
        popExitTransition = {
            when (targetState.destination.route) {
                HOME_SCREEN ->
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        }
    ) {
        DuellmodusRoute(
            onQuizComplete = { duelId ->
                appState
                    .navigate(
                        "DuelResultsScreen/$duelId")
            },
            openScreen = { route -> appState.navigate(route) },
            onNavUp = { appState.popUp() }
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.duellmodusResultsScreen(appState: PhysioQuestAppState) {
    composable(
        route = DUELLMODUS_RESULTS,
        enterTransition = {
            when (initialState.destination.route) {
                DUELLMODUS_ROUTE ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                HOME_SCREEN ->
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        },
        popEnterTransition = {
            when (initialState.destination.route) {
                DUELLMODUS_ROUTE ->
                    slideIntoContainer(
                        AnimatedContentTransitionScope.SlideDirection.Up,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        },
        popExitTransition = {
            when (targetState.destination.route) {
                HOME_SCREEN ->
                    slideOutOfContainer(
                        AnimatedContentTransitionScope.SlideDirection.Down,
                        animationSpec = tween(400)
                    )

                else -> null
            }
        }
    ) {
        DuelResultsScreen(
            duelId = it.arguments?.getString("duelId") ?: "",
            openScreen = { route -> appState.navigate(route) }
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.leaderboardScreen(appState: PhysioQuestAppState) {
    composable(LEADERBOARD_SCREEN) {
        LeaderboardScreen(
            restartApp = { route -> appState.clearAndNavigate(route) },
            openScreen = { route -> appState.navigate(route) }
        )
    }
}

@OptIn(ExperimentalAnimationApi::class)
private fun NavGraphBuilder.accountScreen(appState: PhysioQuestAppState) {
    composable(ACCOUNT_ROUTE) {
        AccountRoute(
            restartApp = { route -> appState.clearAndNavigate(route) },
            openScreen = { route -> appState.navigate(route) }
        )
    }
}

fun NavGraphBuilder.physioQuestGraph(appState: PhysioQuestAppState) {
    startScreen(appState)
    welcomeScreen(appState)

    loginScreen(appState)
    registrationScreen(appState)
    homeScreen(appState)

    lernmodusScreen(appState)
    lernmodusResultsScreen(appState)
    duellmodusScreen(appState)
    duellmodusResultsScreen(appState)

    leaderboardScreen(appState)
    accountScreen(appState)
}