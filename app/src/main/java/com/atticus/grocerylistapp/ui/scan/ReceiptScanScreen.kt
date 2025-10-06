package com.atticus.grocerylistapp.ui.scan

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun ReceiptScanScreen(vm: ReceiptScanViewModel = hiltViewModel()) {
    val ui by vm.ui.collectAsState()
    Column(Modifier.fillMaxSize().padding(16.dp)) {
        Text("Paste receipt text below (MVP). Lines should end with prices like:")
        Text("CHOBANI GREEK YOG 1KG       7.50")
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = ui.rawText,
            onValueChange = vm::onRawChange,
            modifier = Modifier.fillMaxWidth().weight(1f),
            placeholder = { Text("Paste OCR text here...") }
        )
        Spacer(Modifier.height(8.dp))
        Button(onClick = { vm.onIngest(store = null) }) { Text("Ingest") }
        Spacer(Modifier.height(8.dp))
        Text(ui.status)
    }
}
