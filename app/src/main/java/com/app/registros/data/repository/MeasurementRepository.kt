package com.app.registros.data.repository

import android.content.Context
import com.app.registros.data.local.AppDatabase
import com.app.registros.data.local.MeasurementDao
import com.app.registros.data.local.MeasurementEntity
import com.app.registros.data.local.SyncStatus
import com.app.registros.data.model.BodyMeasurement
import com.app.registros.data.sync.SyncManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

/**
 * Repositorio Offline-First para mediciones corporales y plicometría.
 * Las escrituras se guardan inmediatamente en Room (SQLite) en el teléfono
 * y se sincronizan en segundo plano con Supabase cuando hay internet.
 */
class MeasurementRepository(context: Context) {

    private val dao: MeasurementDao = AppDatabase.getDatabase(context).measurementDao()
    val syncManager = SyncManager(context, dao)
    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * Flujo reactivo de mediciones locales para alimentar la interfaz en tiempo real.
     */
    fun getMeasurementsFlow(userId: String): Flow<List<MeasurementEntity>> {
        return dao.getAllMeasurementsFlow(userId)
    }

    suspend fun getMeasurementById(id: String): MeasurementEntity? {
        return dao.getMeasurementById(id)
    }

    /**
     * Guarda la medición inmediatamente en el dispositivo y activa sincronización si hay red.
     */
    suspend fun saveMeasurement(measurement: BodyMeasurement, isNew: Boolean) {
        val status = if (isNew) SyncStatus.PENDING_INSERT else SyncStatus.PENDING_UPDATE
        val entity = MeasurementEntity.fromDomain(measurement, status)
        dao.insertOrReplace(entity)

        // Intento de sincronización en segundo plano sin bloquear al usuario
        measurement.userId?.let { userId ->
            scope.launch {
                syncManager.sync(userId)
            }
        }
    }

    /**
     * Marca para eliminación local e intenta borrar en la nube.
     */
    suspend fun deleteMeasurement(id: String, userId: String) {
        dao.markForDeletion(id)
        scope.launch {
            syncManager.sync(userId)
        }
    }

    /**
     * Sincronización manual bajo demanda (ej. botón 'Refrescar').
     */
    fun triggerManualSync(userId: String) {
        scope.launch {
            syncManager.sync(userId)
        }
    }

    /**
     * Genera un archivo CSV completo con todas las métricas corporales y plicometría
     * para máxima portabilidad (compatible con Excel, Google Sheets, etc.).
     */
    fun exportToCsv(measurements: List<MeasurementEntity>): String {
        val sb = StringBuilder()
        sb.append("ID,FechaHora,PesoKg,GrasaCorporalPct,Notas,")
        sb.append("Cuello,Hombros,Pecho,Cintura,Cadera,BicepsDer,BicepsIzq,AntebrazoDer,AntebrazoIzq,MusloDer,MusloIzq,PantorrillaDer,PantorrillaIzq,")
        sb.append("PliegueTriceps,PliegueSubescapular,PliegueSuprailiaco,PliegueAbdominal,PliegueMuslo,PlieguePectoral,PliegueAxilar,EstadoSincronizacion\n")

        for (m in measurements) {
            val safeNotas = (m.notas ?: "").replace("\"", "\"\"")
            sb.append("\"${m.id}\",\"${m.fechaHora}\",${m.pesoKg},${m.porcentajeGrasa ?: ""},\"$safeNotas\",")
            sb.append("${m.cuello ?: ""},${m.hombros ?: ""},${m.pecho ?: ""},${m.cintura ?: ""},${m.cadera ?: ""},")
            sb.append("${m.bicepsDer ?: ""},${m.bicepsIzq ?: ""},${m.antebrazoDer ?: ""},${m.antebrazoIzq ?: ""},")
            sb.append("${m.musloDer ?: ""},${m.musloIzq ?: ""},${m.pantorrillaDer ?: ""},${m.pantorrillaIzq ?: ""},")
            sb.append("${m.pliegueTriceps ?: ""},${m.pliegueSubescapular ?: ""},${m.pliegueSuprailiaco ?: ""},")
            sb.append("${m.pliegueAbdominal ?: ""},${m.pliegueMuslo ?: ""},${m.plieguePectoral ?: ""},${m.pliegueAxilar ?: ""},")
            sb.append("\"${m.syncStatus}\"\n")
        }
        return sb.toString()
    }
}
