package com.atticus.grocerylistapp.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.datastore.preferences.core.booleanPreferencesKey
import com.atticus.grocerylistapp.repo.dataStore
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import android.content.Context
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import androidx.datastore.preferences.core.edit

private val SHOW_PRICES = booleanPreferencesKey("show_prices")

@HiltViewModel
class SettingsViewModel @Inject constructor(
    @ApplicationContext private val app: Context
): ViewModel() {

    val showPrices: StateFlow<Boolean> =
        app.dataStore.data.map { it[SHOW_PRICES] ?: false }.stateIn(
            viewModelScope, SharingStarted.WhileSubscribed(5_000), false
        )

    fun setShowPrices(enabled: Boolean) = viewModelScope.launch {
        app.dataStore.edit { it[SHOW_PRICES] = enabled }
    }
}
