package com.app.registros.ui.screens.measurements

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Straighten
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.app.registros.data.model.BodyMeasurement
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewMeasurementScreen(
    userId: String,
    viewModel: MeasurementsViewModel,
    onNavigateBack: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabTitles = listOf("General & Peso", "Circunferencias", "Plicometría")

    // Formatter de fecha y hora actual
    val defaultDateTime = remember {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        sdf.format(Date())
    }

    // Datos Básicos
    var fechaHora by remember { mutableStateOf(defaultDateTime) }
    var pesoText by remember { mutableStateOf("") }
    var notas by remember { mutableStateOf("") }

    // Circunferencias (cm)
    var cuello by remember { mutableStateOf("") }
    var hombros by remember { mutableStateOf("") }
    var pecho by remember { mutableStateOf("") }
    var cintura by remember { mutableStateOf("") }
    var cadera by remember { mutableStateOf("") }
    var bicepsDer by remember { mutableStateOf("") }
    var bicepsIzq by remember { mutableStateOf("") }
    var antebrazoDer by remember { mutableStateOf("") }
    var antebrazoIzq by remember { mutableStateOf("") }
    var musloDer by remember { mutableStateOf("") }
    var musloIzq by remember { mutableStateOf("") }
    var pantorrillaDer by remember { mutableStateOf("") }
    var pantorrillaIzq by remember { mutableStateOf("") }

    // Plicometría (mm)
    var pliegueTriceps by remember { mutableStateOf("") }
    var pliegueSubescapular by remember { mutableStateOf("") }
    var pliegueSuprailiaco by remember { mutableStateOf("") }
    var pliegueAbdominal by remember { mutableStateOf("") }
    var pliegueMuslo by remember { mutableStateOf("") }
    var plieguePectoral by remember { mutableStateOf("") }
    var pliegueAxilar by remember { mutableStateOf("") }

    var validationError by remember { mutableStateOf<String?>(null) }

    // Cálculo dinámico de % de grasa corporal Jackson-Pollock
    val estimatedFat: Double? = remember(plieguePectoral, pliegueAbdominal, pliegueMuslo) {
        val pec = plieguePectoral.toDoubleOrNull()
        val abd = pliegueAbdominal.toDoubleOrNull()
        val mus = pliegueMuslo.toDoubleOrNull()
        if (pec != null && abd != null && mus != null && pec > 0 && abd > 0 && mus > 0) {
            val sum = pec + abd + mus
            val density = 1.10938 - (0.0008267 * sum) + (0.0000016 * sum * sum) - (0.0002574 * 28)
            val fat = (495 / density) - 450
            Math.round(fat * 10.0) / 10.0
        } else {
            null
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Nuevo Registro Corporal", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Atrás")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            val peso = pesoText.toDoubleOrNull()
                            if (peso == null || peso <= 0) {
                                validationError = "El peso es obligatorio y debe ser mayor a 0"
                                selectedTab = 0
                                return@IconButton
                            }

                            val measurement = BodyMeasurement(
                                id = UUID.randomUUID().toString(),
                                userId = userId,
                                fechaHora = fechaHora,
                                pesoKg = peso,
                                notas = notas.ifBlank { null },
                                cuello = cuello.toDoubleOrNull(),
                                hombros = hombros.toDoubleOrNull(),
                                pecho = pecho.toDoubleOrNull(),
                                cintura = cintura.toDoubleOrNull(),
                                cadera = cadera.toDoubleOrNull(),
                                bicepsDer = bicepsDer.toDoubleOrNull(),
                                bicepsIzq = bicepsIzq.toDoubleOrNull(),
                                antebrazoDer = antebrazoDer.toDoubleOrNull(),
                                antebrazoIzq = antebrazoIzq.toDoubleOrNull(),
                                musloDer = musloDer.toDoubleOrNull(),
                                musloIzq = musloIzq.toDoubleOrNull(),
                                pantorrillaDer = pantorrillaDer.toDoubleOrNull(),
                                pantorrillaIzq = pantorrillaIzq.toDoubleOrNull(),
                                pliegueTriceps = pliegueTriceps.toDoubleOrNull(),
                                pliegueSubescapular = pliegueSubescapular.toDoubleOrNull(),
                                pliegueSuprailiaco = pliegueSuprailiaco.toDoubleOrNull(),
                                pliegueAbdominal = pliegueAbdominal.toDoubleOrNull(),
                                pliegueMuslo = pliegueMuslo.toDoubleOrNull(),
                                plieguePectoral = plieguePectoral.toDoubleOrNull(),
                                pliegueAxilar = pliegueAxilar.toDoubleOrNull(),
                                porcentajeGrasa = estimatedFat
                            )

                            viewModel.saveMeasurement(measurement, isNew = true, onSuccess = onNavigateBack)
                        }
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Guardar medición", tint = MaterialTheme.colorScheme.primary)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Pestañas organizadas
            TabRow(selectedTabIndex = selectedTab) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) },
                        icon = {
                            when (index) {
                                0 -> Icon(Icons.Default.FitnessCenter, contentDescription = null)
                                1 -> Icon(Icons.Default.Straighten, contentDescription = null)
                                else -> Icon(Icons.Default.Timeline, contentDescription = null)
                            }
                        }
                    )
                }
            }

            if (validationError != null) {
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = validationError ?: "",
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                when (selectedTab) {
                    0 -> {
                        // TAB 0: DATOS BÁSICOS
                        OutlinedTextField(
                            value = fechaHora,
                            onValueChange = { fechaHora = it },
                            label = { Text("Fecha y Hora de Medición") },
                            placeholder = { Text("YYYY-MM-DD HH:MM") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = pesoText,
                            onValueChange = {
                                pesoText = it
                                validationError = null
                            },
                            label = { Text("Peso Corporal (kg) *") },
                            placeholder = { Text("ej. 76.5") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = notas,
                            onValueChange = { notas = it },
                            label = { Text("Notas / Estado de la medición") },
                            placeholder = { Text("ej. en ayunas, post-entrenamiento de fuerza...") },
                            minLines = 3,
                            maxLines = 5,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Card(
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "💡 Guardado Offline Instantáneo",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleSmall
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Esta medición se guardará en tu teléfono inmediatamente y se sincronizará con la nube en cuanto haya conexión a internet.",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            }
                        }
                    }

                    1 -> {
                        // TAB 1: CIRCUNFERENCIAS EN CM
                        Text(
                            text = "Medidas en centímetros (cm)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = cuello,
                                onValueChange = { cuello = it },
                                label = { Text("Cuello (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = hombros,
                                onValueChange = { hombros = it },
                                label = { Text("Hombros (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = pecho,
                                onValueChange = { pecho = it },
                                label = { Text("Pecho / Tórax") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = cintura,
                                onValueChange = { cintura = it },
                                label = { Text("Cintura (cm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = cadera,
                            onValueChange = { cadera = it },
                            label = { Text("Cadera (cm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        Text("Brazos y Antebrazos", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = bicepsDer,
                                onValueChange = { bicepsDer = it },
                                label = { Text("Bíceps Der") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = bicepsIzq,
                                onValueChange = { bicepsIzq = it },
                                label = { Text("Bíceps Izq") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = antebrazoDer,
                                onValueChange = { antebrazoDer = it },
                                label = { Text("Antebrazo Der") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = antebrazoIzq,
                                onValueChange = { antebrazoIzq = it },
                                label = { Text("Antebrazo Izq") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        Text("Piernas y Pantorrillas", fontWeight = FontWeight.SemiBold, style = MaterialTheme.typography.bodyMedium)

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = musloDer,
                                onValueChange = { musloDer = it },
                                label = { Text("Muslo Der") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = musloIzq,
                                onValueChange = { musloIzq = it },
                                label = { Text("Muslo Izq") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = pantorrillaDer,
                                onValueChange = { pantorrillaDer = it },
                                label = { Text("Pantorrilla Der") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = pantorrillaIzq,
                                onValueChange = { pantorrillaIzq = it },
                                label = { Text("Pantorrilla Izq") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    2 -> {
                        // TAB 2: PLICOMETRÍA (PLIEGUES EN MM)
                        Text(
                            text = "Plicometría / Pliegues Cutáneos (mm)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )

                        if (estimatedFat != null) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(
                                            text = "% Grasa Estimado",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                        Text(
                                            text = "$estimatedFat %",
                                            style = MaterialTheme.typography.headlineSmall,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                    Text(
                                        text = "Fórmula Jackson-Pollock",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onPrimaryContainer
                                    )
                                }
                            }
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = plieguePectoral,
                                onValueChange = { plieguePectoral = it },
                                label = { Text("Pectoral (mm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = pliegueAbdominal,
                                onValueChange = { pliegueAbdominal = it },
                                label = { Text("Abdominal (mm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = pliegueMuslo,
                                onValueChange = { pliegueMuslo = it },
                                label = { Text("Muslo Ant. (mm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = pliegueTriceps,
                                onValueChange = { pliegueTriceps = it },
                                label = { Text("Tríceps (mm)") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            OutlinedTextField(
                                value = pliegueSubescapular,
                                onValueChange = { pliegueSubescapular = it },
                                label = { Text("Subescapular") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                            OutlinedTextField(
                                value = pliegueSuprailiaco,
                                onValueChange = { pliegueSuprailiaco = it },
                                label = { Text("Suprailíaco") },
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                                modifier = Modifier.weight(1f)
                            )
                        }

                        OutlinedTextField(
                            value = pliegueAxilar,
                            onValueChange = { pliegueAxilar = it },
                            label = { Text("Axilar Medio (mm)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        val peso = pesoText.toDoubleOrNull()
                        if (peso == null || peso <= 0) {
                            validationError = "El peso es obligatorio y debe ser mayor a 0"
                            selectedTab = 0
                            return@Button
                        }

                        val measurement = BodyMeasurement(
                            id = UUID.randomUUID().toString(),
                            userId = userId,
                            fechaHora = fechaHora,
                            pesoKg = peso,
                            notas = notas.ifBlank { null },
                            cuello = cuello.toDoubleOrNull(),
                            hombros = hombros.toDoubleOrNull(),
                            pecho = pecho.toDoubleOrNull(),
                            cintura = cintura.toDoubleOrNull(),
                            cadera = cadera.toDoubleOrNull(),
                            bicepsDer = bicepsDer.toDoubleOrNull(),
                            bicepsIzq = bicepsIzq.toDoubleOrNull(),
                            antebrazoDer = antebrazoDer.toDoubleOrNull(),
                            antebrazoIzq = antebrazoIzq.toDoubleOrNull(),
                            musloDer = musloDer.toDoubleOrNull(),
                            musloIzq = musloIzq.toDoubleOrNull(),
                            pantorrillaDer = pantorrillaDer.toDoubleOrNull(),
                            pantorrillaIzq = pantorrillaIzq.toDoubleOrNull(),
                            pliegueTriceps = pliegueTriceps.toDoubleOrNull(),
                            pliegueSubescapular = pliegueSubescapular.toDoubleOrNull(),
                            pliegueSuprailiaco = pliegueSuprailiaco.toDoubleOrNull(),
                            pliegueAbdominal = pliegueAbdominal.toDoubleOrNull(),
                            pliegueMuslo = pliegueMuslo.toDoubleOrNull(),
                            plieguePectoral = plieguePectoral.toDoubleOrNull(),
                            pliegueAxilar = pliegueAxilar.toDoubleOrNull(),
                            porcentajeGrasa = estimatedFat
                        )

                        viewModel.saveMeasurement(measurement, isNew = true, onSuccess = onNavigateBack)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Text("Guardar Medición Corporal", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
