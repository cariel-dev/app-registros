package com.app.registros.data.remote

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Proveedor singleton del cliente de Supabase para Android.
 * Conectado al proyecto de producción de Hugo en Supabase (PostgreSQL).
 */
object SupabaseProvider {

    var SUPABASE_URL: String = "https://oyzqrhukwqupsmgcltlf.supabase.co"
    var SUPABASE_ANON_KEY: String = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Im95enFyaHVrd3F1cHNtZ2NsdGxmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3OTA2MzQ0OTYsImV4cCI6MjEwNjIxMDQ5Nn0.pVd6_L_NwZVgE0H42THHdkNvpYmTA1NZSxD9do9A8B0"

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
