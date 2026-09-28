package com.app.registros.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Modelo de datos que representa un registro en la base de datos Supabase.
 * Usa @Serializable para convertir automáticamente entre Kotlin y JSON/Postgres.
 */
@Serializable
data class Record(
    @SerialName("id")
    val id: String? = null,

    @SerialName("user_id")
    val userId: String? = null,

    @SerialName("titulo")
    val titulo: String,

    @SerialName("descripcion")
    val descripcion: String? = null,

    @SerialName("categoria")
    val categoria: String = "General",

    @SerialName("monto")
    val monto: Double = 0.0,

    @SerialName("fecha")
    val fecha: String? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
)
