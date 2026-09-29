package com.app.registros.data.sync

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import com.app.registros.data.local.AppDatabase
import com.app.registros.data.local.MeasurementDao
import com.app.registros.data.local.MeasurementEntity
import com.app.registros.data.local.SyncStatus
import com.app.registros.data.model.BodyMeasurement
import com.app.registros.data.remote.SupabaseProvider
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Gestor de sincronización Offline-First.
 * Escucha cambios de conectividad de red y sube automáticamente a Supabase
 * las mediciones guardadas localmente en el teléfono.
 */
class SyncManager(
    private val context: Context,
    private val dao: MeasurementDao = AppDatabase.getDatabase(context).measurementDao()
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    private val _isOnline = MutableStateFlow(checkInitialConnectivity())
    val isOnline: StateFlow<Boolean> = _isOnline.asStateFlow()

    init {
        registerNetworkCallback()
    }

    private fun checkInitialConnectivity(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val caps = connectivityManager.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }

    private fun registerNetworkCallback() {
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                _isOnline.value = true
                Log.d("SyncManager", "Conexión a internet detectada. Iniciando sincronización...")
            }

            override fun onLost(network: Network) {
                _isOnline.value = false
                Log.d("SyncManager", "Conexión a internet perdida. Modo offline activo.")
            }
        })
    }

    /**
     * Ejecuta una sincronización completa en dos pasos:
     * 1. Sube cambios locales pendientes (INSERT, UPDATE, DELETE) a Supabase.
     * 2. Descarga las mediciones remotas más recientes hacia la base local Room.
     */
    suspend fun sync(userId: String) {
        if (!_isOnline.value) {
            Log.d("SyncManager", "Sin conexión. Saltando sincronización.")
            return
        }

        _isSyncing.value = true
        try {
            val client = SupabaseProvider.client
            val table = client.from("mediciones_corporales")

            // 1. Subir cambios pendientes locales
            val pendingList = dao.getPendingSyncMeasurements(userId)
            for (item in pendingList) {
                try {
                    when (item.syncStatus) {
                        SyncStatus.PENDING_INSERT.name -> {
                            table.insert(item.toDomain())
                            dao.markAsSynced(item.id)
                        }
                        SyncStatus.PENDING_UPDATE.name -> {
                            table.update(item.toDomain()) {
                                filter { eq("id", item.id) }
                            }
                            dao.markAsSynced(item.id)
                        }
                        SyncStatus.PENDING_DELETE.name -> {
                            table.delete {
                                filter { eq("id", item.id) }
                            }
                            dao.deletePermanently(item.id)
                        }
                    }
                } catch (e: Exception) {
                    Log.e("SyncManager", "Error subiendo medición individual ${item.id}", e)
                }
            }

            // 2. Descargar registros de Supabase hacia Room
            val remoteMeasurements = table.select().decodeList<BodyMeasurement>()
            val localEntities = remoteMeasurements.map { measurement ->
                MeasurementEntity.fromDomain(measurement, SyncStatus.SYNCED)
            }
            dao.insertAll(localEntities)

            Log.d("SyncManager", "Sincronización completada exitosamente.")
        } catch (e: Exception) {
            Log.e("SyncManager", "Error durante la sincronización con Supabase", e)
        } finally {
            _isSyncing.value = false
        }
    }
}
