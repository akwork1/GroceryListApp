package com.atticus.grocerylistapp.ui.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun SettingsScreen(
    nav: NavHostController,
    vm: SettingsViewModel = hiltViewModel()
) {
    val showPrices by vm.showPrices.collectAsState()

    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("General", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        Card(Modifier.fillMaxWidth()) {
            Row(Modifier.padding(16.dp)) {
                Column(Modifier.weight(1f)) {
                    Text("Show prices")
                    Text("When off, the list stays minimal.", style = MaterialTheme.typography.bodySmall)
                }
                Switch(checked = showPrices, onCheckedChange = vm::setShowPrices)
            }
        }

        Spacer(Modifier.height(24.dp))
        Text("More", style = MaterialTheme.typography.titleLarge)
        Spacer(Modifier.height(12.dp))

        // These now navigate to real screens
        ListTileButton(title = "History", subtitle = "Past purchases & receipts") {
            nav.navigate("history")
        }
        Spacer(Modifier.height(8.dp))
        ListTileButton(title = "Analytics", subtitle = "Spending by week/month/item") {
            nav.navigate("analytics")
        }
        Spacer(Modifier.height(8.dp))
        ListTileButton(title = "Scan receipt", subtitle = "OCR a new receipt") {
            nav.navigate("scan")
        }
    }
}

@Composable
private fun ListTileButton(
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(2.dp))
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
        }
    }
}
