package com.app.registros.ui.navigation

/**
 * Rutas de navegación de la aplicación de medidas corporales.
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object MeasurementsList : Screen("measurements_list")
    object NewMeasurement : Screen("new_measurement")
    object Export : Screen("export")
}
