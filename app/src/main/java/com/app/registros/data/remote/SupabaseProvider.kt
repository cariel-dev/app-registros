package com.app.registros.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Proveedor singleton del cliente de Supabase.
 * Centraliza la conexión con PostgreSQL y el servicio de Autenticación.
 */
object SupabaseProvider {

    /**
     * Reemplaza estos valores con los de tu proyecto de Supabase.
     * Puedes encontrarlos en:
     * https://app.supabase.com -> Selecciona tu Proyecto -> Project Settings -> API
     */
    var SUPABASE_URL: String = "https://xyzcompany.supabase.co"
    var SUPABASE_ANON_KEY: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = SUPABASE_URL,
            supabaseKey = SUPABASE_ANON_KEY
        ) {
            install(Auth)
            install(Postgrest)
        }
    }
}
