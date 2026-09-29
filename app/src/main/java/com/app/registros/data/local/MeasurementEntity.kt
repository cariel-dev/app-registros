package com.app.registros.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.app.registros.data.model.BodyMeasurement

enum class SyncStatus {
    SYNCED,
    PENDING_INSERT,
    PENDING_UPDATE,
    PENDING_DELETE
}

/**
 * Entidad de Room DB almacenada localmente en SQLite en el teléfono.
 * Permite que la app funcione 100% offline y almacene el estado de sincronización.
 */
@Entity(tableName = "local_mediciones")
data class MeasurementEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val fechaHora: String,
    val pesoKg: Double,
    val notas: String?,

    // Circunferencias (cm)
    val cuello: Double?,
    val hombros: Double?,
    val pecho: Double?,
    val cintura: Double?,
    val cadera: Double?,
    val bicepsDer: Double?,
    val bicepsIzq: Double?,
    val antebrazoDer: Double?,
    val antebrazoIzq: Double?,
    val musloDer: Double?,
    val musloIzq: Double?,
    val pantorrillaDer: Double?,
    val pantorrillaIzq: Double?,

    // Plicometría (mm)
    val pliegueTriceps: Double?,
    val pliegueSubescapular: Double?,
    val pliegueSuprailiaco: Double?,
    val pliegueAbdominal: Double?,
    val pliegueMuslo: Double?,
    val plieguePectoral: Double?,
    val pliegueAxilar: Double?,
    val porcentajeGrasa: Double?,

    // Control de sincronización Offline-First
    val syncStatus: String = SyncStatus.SYNCED.name,
    val lastModified: Long = System.currentTimeMillis()
) {
    fun toDomain(): BodyMeasurement = BodyMeasurement(
        id = id,
        userId = userId,
        fechaHora = fechaHora,
        pesoKg = pesoKg,
        notas = notas,
        cuello = cuello,
        hombros = hombros,
        pecho = pecho,
        cintura = cintura,
        cadera = cadera,
        bicepsDer = bicepsDer,
        bicepsIzq = bicepsIzq,
        antebrazoDer = antebrazoDer,
        antebrazoIzq = antebrazoIzq,
        musloDer = musloDer,
        musloIzq = musloIzq,
        pantorrillaDer = pantorrillaDer,
        pantorrillaIzq = pantorrillaIzq,
        pliegueTriceps = pliegueTriceps,
        pliegueSubescapular = pliegueSubescapular,
        pliegueSuprailiaco = pliegueSuprailiaco,
        pliegueAbdominal = pliegueAbdominal,
        pliegueMuslo = pliegueMuslo,
        plieguePectoral = plieguePectoral,
        pliegueAxilar = pliegueAxilar,
        porcentajeGrasa = porcentajeGrasa
    )

    companion object {
        fun fromDomain(model: BodyMeasurement, syncStatus: SyncStatus = SyncStatus.SYNCED): MeasurementEntity {
            return MeasurementEntity(
                id = model.id,
                userId = model.userId ?: "",
                fechaHora = model.fechaHora,
                pesoKg = model.pesoKg,
                notas = model.notas,
                cuello = model.cuello,
                hombros = model.hombros,
                pecho = model.pecho,
                cintura = model.cintura,
                cadera = model.cadera,
                bicepsDer = model.bicepsDer,
                bicepsIzq = model.bicepsIzq,
                antebrazoDer = model.antebrazoDer,
                antebrazoIzq = model.antebrazoIzq,
                musloDer = model.musloDer,
                musloIzq = model.musloIzq,
                pantorrillaDer = model.pantorrillaDer,
                pantorrillaIzq = model.pantorrillaIzq,
                pliegueTriceps = model.pliegueTriceps,
                pliegueSubescapular = model.pliegueSubescapular,
                pliegueSuprailiaco = model.pliegueSuprailiaco,
                pliegueAbdominal = model.pliegueAbdominal,
                pliegueMuslo = model.pliegueMuslo,
                plieguePectoral = model.plieguePectoral,
                pliegueAxilar = model.pliegueAxilar,
                porcentajeGrasa = model.porcentajeGrasa,
                syncStatus = syncStatus.name
            )
        }
    }
}
