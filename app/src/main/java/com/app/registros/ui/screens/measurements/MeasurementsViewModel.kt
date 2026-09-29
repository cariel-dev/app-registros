package com.app.registros.ui.screens.measurements

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.app.registros.data.local.MeasurementEntity
import com.app.registros.data.model.BodyMeasurement
import com.app.registros.data.repository.MeasurementRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MeasurementsUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedMeasurement: MeasurementEntity? = null
)

class MeasurementsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MeasurementRepository(application.applicationContext)
    private val _uiState = MutableStateFlow(MeasurementsUiState())
    val uiState: StateFlow<MeasurementsUiState> = _uiState.asStateFlow()

    val isSyncing: StateFlow<Boolean> = repository.syncManager.isSyncing
    val isOnline: StateFlow<Boolean> = repository.syncManager.isOnline

    private var currentUserId: String = ""

    fun setUserId(userId: String) {
        if (currentUserId != userId) {
            currentUserId = userId
            repository.triggerManualSync(userId)
        }
    }

    /**
     * Flujo reactivo con las mediciones locales del usuario actual.
     */
    fun getMeasurementsFlow(userId: String): StateFlow<List<MeasurementEntity>> {
        return repository.getMeasurementsFlow(userId).stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )
    }

    fun saveMeasurement(
        measurement: BodyMeasurement,
        isNew: Boolean,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                repository.saveMeasurement(measurement, isNew)
                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error al guardar en el teléfono"
                )
            }
        }
    }

    fun deleteMeasurement(id: String, userId: String) {
        viewModelScope.launch {
            repository.deleteMeasurement(id, userId)
        }
    }

    fun syncNow(userId: String) {
        repository.triggerManualSync(userId)
    }

    suspend fun getMeasurementById(id: String): MeasurementEntity? {
        return repository.getMeasurementById(id)
    }

    fun exportToCsv(measurements: List<MeasurementEntity>): String {
        return repository.exportToCsv(measurements)
    }
}
