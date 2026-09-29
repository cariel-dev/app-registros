package com.app.registros.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Modelo de datos de medición corporal y plicometría compatible con Supabase (JSON/Postgres)
 * y con la base de datos local SQLite (Room).
 */
@Serializable
data class BodyMeasurement(
    @SerialName("id")
    val id: String = UUID.randomUUID().toString(),

    @SerialName("user_id")
    val userId: String? = null,

    @SerialName("fecha_hora")
    val fechaHora: String,

    @SerialName("peso_kg")
    val pesoKg: Double,

    @SerialName("notas")
    val notas: String? = null,

    // Circunferencias en centímetros (cm)
    @SerialName("cuello")
    val cuello: Double? = null,

    @SerialName("hombros")
    val hombros: Double? = null,

    @SerialName("pecho")
    val pecho: Double? = null,

    @SerialName("cintura")
    val cintura: Double? = null,

    @SerialName("cadera")
    val cadera: Double? = null,

    @SerialName("biceps_der")
    val bicepsDer: Double? = null,

    @SerialName("biceps_izq")
    val bicepsIzq: Double? = null,

    @SerialName("antebrazo_der")
    val antebrazoDer: Double? = null,

    @SerialName("antebrazo_izq")
    val antebrazoIzq: Double? = null,

    @SerialName("muslo_der")
    val musloDer: Double? = null,

    @SerialName("muslo_izq")
    val musloIzq: Double? = null,

    @SerialName("pantorrilla_der")
    val pantorrillaDer: Double? = null,

    @SerialName("pantorrilla_izq")
    val pantorrillaIzq: Double? = null,

    // Plicometría / Pliegues cutáneos en milímetros (mm)
    @SerialName("pliegue_triceps")
    val pliegueTriceps: Double? = null,

    @SerialName("pliegue_subescapular")
    val pliegueSubescapular: Double? = null,

    @SerialName("pliegue_suprailiaco")
    val pliegueSuprailiaco: Double? = null,

    @SerialName("pliegue_abdominal")
    val pliegueAbdominal: Double? = null,

    @SerialName("pliegue_muslo")
    val pliegueMuslo: Double? = null,

    @SerialName("pliegue_pectoral")
    val plieguePectoral: Double? = null,

    @SerialName("pliegue_axilar")
    val pliegueAxilar: Double? = null,

    // Porcentaje de grasa estimado (%)
    @SerialName("porcentaje_grasa")
    val porcentajeGrasa: Double? = null,

    @SerialName("created_at")
    val createdAt: String? = null,

    @SerialName("updated_at")
    val updatedAt: String? = null
) {
    /**
     * Calcula una estimación del porcentaje de grasa corporal utilizando la fórmula
     * Jackson-Pollock de 3 pliegues si están disponibles (Pecho/Tríceps, Abdomen, Muslo).
     */
    fun estimateBodyFat(ageYears: Int = 28, isMale: Boolean = true): Double? {
        val pTriceps = pliegueTriceps ?: 0.0
        val pAbdominal = pliegueAbdominal ?: 0.0
        val pMuslo = pliegueMuslo ?: 0.0
        val pPectoral = plieguePectoral ?: 0.0

        if (isMale && pPectoral > 0 && pAbdominal > 0 && pMuslo > 0) {
            val sum = pPectoral + pAbdominal + pMuslo
            val density = 1.10938 - (0.0008267 * sum) + (0.0000016 * sum * sum) - (0.0002574 * ageYears)
            val fatPercentage = (495 / density) - 450
            return (Math.round(fatPercentage * 10.0) / 10.0).coerceIn(3.0, 50.0)
        } else if (!isMale && pTriceps > 0 && (pliegueSuprailiaco ?: 0.0) > 0 && pMuslo > 0) {
            val sum = pTriceps + (pliegueSuprailiaco ?: 0.0) + pMuslo
            val density = 1.0994921 - (0.0009929 * sum) + (0.0000023 * sum * sum) - (0.0001392 * ageYears)
            val fatPercentage = (495 / density) - 450
            return (Math.round(fatPercentage * 10.0) / 10.0).coerceIn(8.0, 55.0)
        }
        return porcentajeGrasa
    }
}
