package com.app.registros.data.repository

import com.app.registros.data.model.Record
import com.app.registros.data.remote.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order

/**
 * Repositorio para la gestión de registros (CRUD) en PostgreSQL/Supabase.
 * Incluye utilidades de exportación para garantizar portabilidad total de los datos.
 */
class RecordRepository {

    private val client = SupabaseProvider.client
    private val table = client.from("registros")

    /**
     * Obtiene todos los registros del usuario actual ordenados por fecha descendente.
     */
    suspend fun getRecords(): Result<List<Record>> {
        return try {
            val records = table.select {
                order(column = "fecha", order = Order.DESCENDING)
            }.decodeList<Record>()
            Result.success(records)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Inserta un nuevo registro asignando automáticamente el user_id autenticado.
     */
    suspend fun addRecord(record: Record): Result<Unit> {
        return try {
            val currentUserId = client.auth.currentUserOrNull()?.id
                ?: throw IllegalStateException("Debes iniciar sesión para guardar registros")

            val recordToInsert = record.copy(userId = currentUserId)
            table.insert(recordToInsert)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Actualiza un registro existente.
     */
    suspend fun updateRecord(record: Record): Result<Unit> {
        return try {
            val id = record.id ?: throw IllegalArgumentException("Se requiere el ID para actualizar")
            table.update(record) {
                filter {
                    eq("id", id)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Elimina un registro por su ID.
     */
    suspend fun deleteRecord(id: String): Result<Unit> {
        return try {
            table.delete {
                filter {
                    eq("id", id)
                }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Exporta la lista de registros a formato estándar CSV.
     * Facilita extraer tus datos en cualquier momento hacia Excel, Google Sheets u otras apps.
     */
    fun exportToCsv(records: List<Record>): String {
        val sb = StringBuilder()
        sb.append("ID,Titulo,Descripcion,Categoria,Monto,Fecha\n")
        records.forEach { r ->
            val safeTitle = r.titulo.replace("\"", "\"\"")
            val safeDesc = (r.descripcion ?: "").replace("\"", "\"\"")
            sb.append("\"${r.id.orEmpty()}\",\"$safeTitle\",\"$safeDesc\",\"${r.categoria}\",${r.monto},\"${r.fecha.orEmpty()}\"\n")
        }
        return sb.toString()
    }
}
