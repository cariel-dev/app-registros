package com.app.registros.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.app.registros.ui.screens.auth.AuthViewModel
import com.app.registros.ui.screens.auth.LoginScreen
import com.app.registros.ui.screens.auth.RegisterScreen
import com.app.registros.ui.screens.export.ExportScreen
import com.app.registros.ui.screens.measurements.MeasurementsListScreen
import com.app.registros.ui.screens.measurements.MeasurementsViewModel
import com.app.registros.ui.screens.measurements.NewMeasurementScreen

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    measurementsViewModel: MeasurementsViewModel = viewModel()
) {
    val authState by authViewModel.uiState.collectAsState()

    val initialRoute = if (authState.isAuthenticated) {
        Screen.MeasurementsList.route
    } else {
        Screen.Login.route
    }

    val currentUserId = authState.userId ?: ""
    val currentUsername = authState.username ?: "Usuario"

    NavHost(
        navController = navController,
        startDestination = initialRoute
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                viewModel = authViewModel,
                onNavigateToRegister = {
                    navController.navigate(Screen.Register.route)
                },
                onLoginSuccess = {
                    navController.navigate(Screen.MeasurementsList.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                viewModel = authViewModel,
                onNavigateBack = { navController.popBackStack() },
                onRegisterSuccess = {
                    navController.navigate(Screen.MeasurementsList.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.MeasurementsList.route) {
            MeasurementsListScreen(
                userId = currentUserId,
                username = currentUsername,
                viewModel = measurementsViewModel,
                onNavigateToNew = {
                    navController.navigate(Screen.NewMeasurement.route)
                },
                onNavigateToExport = {
                    navController.navigate(Screen.Export.route)
                },
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.MeasurementsList.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.NewMeasurement.route) {
            NewMeasurementScreen(
                userId = currentUserId,
                viewModel = measurementsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Export.route) {
            ExportScreen(
                userId = currentUserId,
                viewModel = measurementsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
