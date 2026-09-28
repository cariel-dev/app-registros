package com.app.registros.ui.screens.records

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditRecordScreen(
    recordId: String?,
    viewModel: RecordsViewModel,
    onNavigateBack: () -> Unit
) {
    val existing = remember(recordId) {
        recordId?.let { viewModel.getRecordById(it) }
    }

    var title by remember { mutableStateOf(existing?.titulo ?: "") }
    var description by remember { mutableStateOf(existing?.descripcion ?: "") }
    var category by remember { mutableStateOf(existing?.categoria ?: "General") }
    var amountText by remember { mutableStateOf(existing?.monto?.toString() ?: "0.0") }
    var validationError by remember { mutableStateOf<String?>(null) }

    val categories = listOf("General", "Finanzas", "Trabajo", "Personal", "Otro")
    var categoryDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (recordId == null) "Nuevo Registro" else "Editar Registro") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (title.isBlank()) {
                                validationError = "El título es obligatorio"
                                return@IconButton
                            }
                            val amount = amountText.toDoubleOrNull() ?: 0.0
                            viewModel.saveRecord(
                                id = recordId,
                                title = title.trim(),
                                description = description.trim(),
                                category = category,
                                amount = amount,
                                onSuccess = onNavigateBack
                            )
                        }
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Guardar")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                    validationError = null
                },
                label = { Text("Título *") },
                isError = validationError != null,
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Selector de Categoría
            ExposedDropdownMenuBox(
                expanded = categoryDropdownExpanded,
                onExpandedChange = { categoryDropdownExpanded = !categoryDropdownExpanded }
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Categoría") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryDropdownExpanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = categoryDropdownExpanded,
                    onDismissRequest = { categoryDropdownExpanded = false }
                ) {
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                category = cat
                                categoryDropdownExpanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Monto o Valor Numérico (Opcional)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción / Notas adicionales") },
                minLines = 4,
                maxLines = 6,
                modifier = Modifier.fillMaxWidth()
            )

            if (validationError != null) {
                Text(
                    text = validationError ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    if (title.isBlank()) {
                        validationError = "El título es obligatorio"
                        return@Button
                    }
                    val amount = amountText.toDoubleOrNull() ?: 0.0
                    viewModel.saveRecord(
                        id = recordId,
                        title = title.trim(),
                        description = description.trim(),
                        category = category,
                        amount = amount,
                        onSuccess = onNavigateBack
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text(if (recordId == null) "Crear Registro" else "Actualizar Registro", fontWeight = FontWeight.Bold)
            }
        }
    }
}
