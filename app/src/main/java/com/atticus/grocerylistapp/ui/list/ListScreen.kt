package com.atticus.grocerylistapp.ui.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController

@Composable
fun ListScreen(
    nav: NavHostController,
    vm: ListViewModel = hiltViewModel()
) {
    val ui by vm.ui.collectAsState()

    Box(Modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().padding(16.dp)) {

            // Search box
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(10.dp)) {
                    OutlinedTextField(
                        value = ui.query,
                        onValueChange = vm::onQuery,
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Search or add item…") },
                        singleLine = true
                    )
                    if (ui.matches.isNotEmpty()) {
                        Spacer(Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(ui.matches) { name ->
                                AssistChip(
                                    onClick = { vm.addManualOrSelected(name) },
                                    label = { Text(name) }
                                )
                            }
                        }
                    }
                }
            }

            // Add button — centered under search
            Box(Modifier.fillMaxWidth()) {
                FilledTonalButton(
                    onClick = { vm.addManualOrSelected() },
                    modifier = Modifier.align(Alignment.Center).padding(top = 10.dp)
                ) {
                    Text("+  Add item")
                }
            }

            Spacer(Modifier.height(16.dp))
            Text("My List", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))

            val active = ui.current.filter { it.status == "active" }
            val bought = ui.current.filter { it.status == "bought" }
            val oos    = ui.current.filter { it.status == "oos" }

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(bottom = 80.dp) // leave room for bottom Done
            ) {
                items(active, key = { it.id }) { line ->
                    ListRow(
                        name = line.name,
                        status = "active",
                        onBought = { vm.markBought(line.id) },
                        onOOS = { vm.markOOS(line.id) },
                        onDelete = { vm.removeLine(line.id) }
                    )
                }

                if (bought.isNotEmpty()) {
                    item { SectionHeader("Bought") }
                    items(bought, key = { it.id }) { line ->
                        ListRow(
                            name = line.name,
                            status = "bought",
                            onBought = { /* already bought */ },
                            onOOS = { vm.markOOS(line.id) },
                            onDelete = { vm.removeLine(line.id) }
                        )
                    }
                }

                if (oos.isNotEmpty()) {
                    item { SectionHeader("Out of stock") }
                    items(oos, key = { it.id }) { line ->
                        ListRow(
                            name = line.name,
                            status = "oos",
                            onBought = { vm.markBought(line.id) },
                            onOOS = { /* already oos */ },
                            onDelete = { vm.removeLine(line.id) }
                        )
                    }
                }
            }
        }

        // ✅ Bottom–center Done button: only shows if there’s something to finalize
        val showDone = ui.current.any { it.status == "bought" || it.status == "oos" }
        if (showDone) {
            FilledTonalButton(
                onClick = vm::clearDone,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 16.dp)
            ) {
                Text("Done")
            }
        }
    }
}

@Composable private fun SectionHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
        modifier = Modifier.padding(top = 12.dp)
    )
}

@Composable
private fun ListRow(
    name: String,
    status: String,
    onBought: () -> Unit,
    onOOS: () -> Unit,
    onDelete: () -> Unit
) {
    val bg = when (status) {
        "bought" -> MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
        "oos"    -> Color(0xFFCF6679).copy(alpha = 0.18f)
        else     -> MaterialTheme.colorScheme.surface
    }
    Surface(color = bg, shape = MaterialTheme.shapes.medium) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(name, modifier = Modifier.weight(1f), style = MaterialTheme.typography.bodyLarge)
            TextButton(onClick = onBought, enabled = status != "bought") { Text("✓") }
            TextButton(onClick = onOOS,    enabled = status != "oos")    { Text("OOS") }
            TextButton(onClick = onDelete) { Text("✕") }
        }
    }
}
