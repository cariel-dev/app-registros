package com.app.registros.data.repository

import com.app.registros.data.remote.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo

/**
 * Repositorio de autenticación que gestiona el registro, login y sesiones
 * utilizando Supabase Auth.
 */
class AuthRepository {

    private val auth = SupabaseProvider.client.auth

    /**
     * Registra un nuevo usuario con correo y contraseña.
     */
    suspend fun signUp(email: String, pass: String): Result<Unit> {
        return try {
            auth.signUpWith(Email) {
                this.email = email
                this.password = pass
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Inicia sesión con correo y contraseña.
     */
    suspend fun signIn(email: String, pass: String): Result<Unit> {
        return try {
            auth.signInWith(Email) {
                this.email = email
                this.password = pass
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Cierra la sesión activa.
     */
    suspend fun signOut(): Result<Unit> {
        return try {
            auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Retorna la información del usuario autenticado o null si no hay sesión.
     */
    fun getCurrentUser(): UserInfo? {
        return auth.currentUserOrNull()
    }

    /**
     * Verifica si hay una sesión activa.
     */
    fun isUserLoggedIn(): Boolean {
        return auth.currentUserOrNull() != null
    }
}
