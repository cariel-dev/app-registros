package com.app.registros.data.repository

import com.app.registros.data.remote.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.user.UserInfo

/**
 * Repositorio de autenticación simplificada (Usuario + Contraseña).
 * Elimina la fricción de solicitar correos o confirmaciones de email al usuario.
 */
class AuthRepository {

    private val auth = SupabaseProvider.client.auth

    /**
     * Convierte el nombre de usuario ingresado en una identidad compatible con Supabase Auth.
     * Ejemplo: "hugo" -> "hugo@appregistros.local"
     */
    private fun toInternalEmail(username: String): String {
        val sanitized = username.trim().lowercase().replace(" ", "_")
        return "$sanitized@appregistros.local"
    }

    /**
     * Registra un nuevo usuario usando únicamente su nombre de usuario y contraseña.
     */
    suspend fun signUp(username: String, pass: String): Result<Unit> {
        return try {
            val internalEmail = toInternalEmail(username)
            auth.signUpWith(Email) {
                this.email = internalEmail
                this.password = pass
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Inicia sesión usando únicamente su nombre de usuario y contraseña.
     */
    suspend fun signIn(username: String, pass: String): Result<Unit> {
        return try {
            val internalEmail = toInternalEmail(username)
            auth.signInWith(Email) {
                this.email = internalEmail
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
     * Retorna el nombre de usuario actual sin el sufijo interno.
     */
    fun getCurrentUsername(): String? {
        val email = auth.currentUserOrNull()?.email ?: return null
        return email.substringBefore("@")
    }

    fun getCurrentUserId(): String? {
        return auth.currentUserOrNull()?.id
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUserOrNull() != null
    }
}
