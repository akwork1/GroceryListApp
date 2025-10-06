package com.atticus.grocerylistapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import com.atticus.grocerylistapp.ui.AppRoot
import com.atticus.grocerylistapp.ui.theme.GroceryTheme
import dagger.hilt.android.AndroidEntryPoint
import androidx.compose.material3.ExperimentalMaterial3Api

@AndroidEntryPoint
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GroceryTheme {
                Surface /* (experimental in your current M3 version) */ (
                    // if you want full-screen surface:
                    // modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppRoot()
                }
            }
        }
    }
}
