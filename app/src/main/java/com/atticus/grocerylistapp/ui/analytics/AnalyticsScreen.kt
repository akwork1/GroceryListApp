package com.atticus.grocerylistapp.ui.analytics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun AnalyticsScreen(vm: AnalyticsViewModel = hiltViewModel()) {
    val ui by vm.ui.collectAsState()
    Column(Modifier.padding(16.dp)) {
        Card(Modifier.padding(8.dp)) {
            Text("This week total: $${"%.2f".format(ui.weekTotal)}", modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.titleMedium)
        }
        Card(Modifier.padding(8.dp)) {
            Text("This month total: $${"%.2f".format(ui.monthTotal)}", modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.titleMedium)
        }
        Text("Top items this month", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(8.dp))
        ui.topItems.forEach { (name, sum) ->
            Text("- $name: $${"%.2f".format(sum)}", modifier = Modifier.padding(horizontal = 12.dp, vertical = 2.dp))
        }
    }
}
