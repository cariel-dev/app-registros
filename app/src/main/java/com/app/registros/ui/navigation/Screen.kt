package com.app.registros.ui.navigation

/**
 * Rutas de navegación de la aplicación.
 */
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object RecordsList : Screen("records_list")
    object AddRecord : Screen("add_record")
    object EditRecord : Screen("edit_record/{recordId}") {
        fun createRoute(recordId: String) = "edit_record/$recordId"
    }
    object Export : Screen("export")
}
