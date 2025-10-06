package com.atticus.grocerylistapp.ui.history

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    nav: NavHostController,
    vm: HistoryViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsState()
    val sdf = remember { SimpleDateFormat("EEE, d MMM yyyy", Locale.getDefault()) }

    var showPicker by remember { mutableStateOf(false) }
    if (showPicker) {
        val state = rememberDatePickerState(initialSelectedDateMillis = ui.selectedDayStart)
        DatePickerDialog(
            onDismissRequest = {
                state.selectedDateMillis?.let { vm.onDayChange(HistoryViewModel.startOfDay(it)) }
                showPicker = false
            },
            confirmButton = {
                TextButton(onClick = {
                    state.selectedDateMillis?.let { vm.onDayChange(HistoryViewModel.startOfDay(it)) }
                    showPicker = false
                }) { Text("Done") }
            }
        ) {
            DatePicker(state = state)
        }
    }

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Previous lists", style = MaterialTheme.typography.titleLarge)
            TextButton(onClick = { showPicker = true }) {
                Text(sdf.format(Date(ui.selectedDayStart)))
            }
        }

        Spacer(Modifier.height(8.dp))

        ElevatedCard {
            Column(Modifier.padding(12.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("This day", style = MaterialTheme.typography.titleMedium)
                    Text("Bought $${"%.2f".format(ui.totalBought)}", style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.heightIn(min = 80.dp).fillMaxWidth()
                ) {
                    items(ui.items) { item ->
                        val bg = when (item.type) {
                            "oos" -> Color(0xFFCF6679).copy(alpha = 0.18f) // highlighted red for OOS
                            "bought" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.10f) // subtle green for bought
                            else -> MaterialTheme.colorScheme.surface
                        }
                        Surface(color = bg, shape = MaterialTheme.shapes.medium) {
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(Modifier.weight(1f)) {
                                    Text(item.name, style = MaterialTheme.typography.bodyLarge)
                                    val sub = when (item.type) {
                                        "NA" -> "Out of stock"
                                        "bought" -> item.price?.let { "$" + String.format("%.2f", it) } ?: "Bought"
                                        else -> ""
                                    }
                                    if (sub.isNotBlank()) {
                                        Text(sub, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                                TextButton(onClick = { vm.addOne(item.itemId) }) { Text("Add") }
                            }
                        }
                    }
                }
            }
        }
    }
}
