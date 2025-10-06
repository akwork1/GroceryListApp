package com.atticus.grocerylistapp.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.atticus.grocerylistapp.ui.analytics.AnalyticsScreen
import com.atticus.grocerylistapp.ui.history.HistoryScreen
import com.atticus.grocerylistapp.ui.list.ListScreen
import com.atticus.grocerylistapp.ui.scan.ReceiptScanScreen
import com.atticus.grocerylistapp.ui.settings.SettingsScreen

@ExperimentalMaterial3Api
@Composable
fun AppRoot() {
    val nav = rememberNavController()
    val backStackEntry by nav.currentBackStackEntryAsState()
    val route = backStackEntry?.destination?.route ?: "list"

    val canGoBack = route != "list"
    val title = when (route) {
        "list" -> "Grocery List"
        "settings" -> "Settings"
        "history" -> "History"
        "analytics" -> "Analytics"
        "scan" -> "Scan Receipt"
        else -> "Grocery list"
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(title) },
                navigationIcon = {
                    if (canGoBack) {
                        IconButton(onClick = { nav.popBackStack() }) {
                            Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                        }
                    }
                },
                actions = {
                    if (!canGoBack) {
                        IconButton(onClick = { nav.navigate("history") }) {
                            Icon(Icons.Filled.History, contentDescription = "History")
                        }
                        IconButton(onClick = { nav.navigate("settings") }) {
                            Icon(Icons.Filled.Settings, contentDescription = "Settings")
                        }
                    }
                }
            )
        }
    ) { padding ->
        NavHost(nav, startDestination = "list", modifier = Modifier.padding(padding)) {
            composable("list") { ListScreen(nav = nav) }
            composable("history") { HistoryScreen(nav = nav) }
            composable("settings") { SettingsScreen(nav = nav) }
            composable("analytics") { AnalyticsScreen() }
            composable("scan") { ReceiptScanScreen() }
        }
    }
}
