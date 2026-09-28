package com.app.registros.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.registros.ui.screens.auth.AuthViewModel
import com.app.registros.ui.screens.auth.LoginScreen
import com.app.registros.ui.screens.auth.RegisterScreen
import com.app.registros.ui.screens.export.ExportScreen
import com.app.registros.ui.screens.records.AddEditRecordScreen
import com.app.registros.ui.screens.records.RecordsListScreen
import com.app.registros.ui.screens.records.RecordsViewModel

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    authViewModel: AuthViewModel = viewModel(),
    recordsViewModel: RecordsViewModel = viewModel()
) {
    val initialRoute = if (authViewModel.uiState.value.isAuthenticated) {
        Screen.RecordsList.route
    } else {
        Screen.Login.route
    }

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
                    recordsViewModel.loadRecords()
                    navController.navigate(Screen.RecordsList.route) {
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
                    recordsViewModel.loadRecords()
                    navController.navigate(Screen.RecordsList.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.RecordsList.route) {
            RecordsListScreen(
                viewModel = recordsViewModel,
                onNavigateToAddRecord = {
                    navController.navigate(Screen.AddRecord.route)
                },
                onNavigateToEditRecord = { recordId ->
                    navController.navigate(Screen.EditRecord.createRoute(recordId))
                },
                onNavigateToExport = {
                    navController.navigate(Screen.Export.route)
                },
                onLogout = {
                    authViewModel.signOut()
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.RecordsList.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.AddRecord.route) {
            AddEditRecordScreen(
                recordId = null,
                viewModel = recordsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(
            route = Screen.EditRecord.route,
            arguments = listOf(navArgument("recordId") { type = NavType.StringType })
        ) { backStackEntry ->
            val recordId = backStackEntry.arguments?.getString("recordId")
            AddEditRecordScreen(
                recordId = recordId,
                viewModel = recordsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(Screen.Export.route) {
            ExportScreen(
                viewModel = recordsViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
