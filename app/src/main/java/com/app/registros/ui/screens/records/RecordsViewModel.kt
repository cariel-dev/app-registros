package com.app.registros.ui.screens.records

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.registros.data.model.Record
import com.app.registros.data.repository.RecordRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class RecordsUiState(
    val records: List<Record> = emptyList(),
    val filteredRecords: List<Record> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedCategory: String = "Todas",
    val searchQuery: String = ""
)

class RecordsViewModel(
    private val repository: RecordRepository = RecordRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecordsUiState())
    val uiState: StateFlow<RecordsUiState> = _uiState.asStateFlow()

    init {
        loadRecords()
    }

    fun loadRecords() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val result = repository.getRecords()
            result.onSuccess { list ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    records = list
                )
                applyFilters()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.localizedMessage ?: "Error al cargar registros"
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applyFilters()
    }

    fun onCategorySelected(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        applyFilters()
    }

    private fun applyFilters() {
        val current = _uiState.value
        val filtered = current.records.filter { record ->
            val matchesCategory = current.selectedCategory == "Todas" || record.categoria.equals(current.selectedCategory, ignoreCase = true)
            val matchesSearch = current.searchQuery.isBlank() ||
                    record.titulo.contains(current.searchQuery, ignoreCase = true) ||
                    (record.descripcion ?: "").contains(current.searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
        _uiState.value = _uiState.value.copy(filteredRecords = filtered)
    }

    fun saveRecord(
        id: String?,
        title: String,
        description: String,
        category: String,
        amount: Double,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val record = Record(
                id = id,
                titulo = title,
                descripcion = description.ifBlank { null },
                categoria = category.ifBlank { "General" },
                monto = amount
            )

            val result = if (id == null) {
                repository.addRecord(record)
            } else {
                repository.updateRecord(record)
            }

            result.onSuccess {
                loadRecords()
                onSuccess()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.localizedMessage ?: "Error al guardar el registro"
                )
            }
        }
    }

    fun deleteRecord(id: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val result = repository.deleteRecord(id)
            result.onSuccess {
                loadRecords()
            }.onFailure { error ->
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = error.localizedMessage ?: "Error al eliminar"
                )
            }
        }
    }

    fun getRecordById(id: String): Record? {
        return _uiState.value.records.find { it.id == id }
    }

    fun exportAsCsv(): String {
        return repository.exportToCsv(_uiState.value.records)
    }
}
