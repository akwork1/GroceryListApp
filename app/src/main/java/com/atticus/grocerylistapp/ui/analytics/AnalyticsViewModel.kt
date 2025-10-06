package com.atticus.grocerylistapp.ui.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atticus.grocerylistapp.repo.GroceryRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val repo: GroceryRepo
): ViewModel() {

    data class UiState(
        val weekTotal: Double = 0.0,
        val monthTotal: Double = 0.0,
        val topItems: List<Pair<String, Double>> = emptyList()
    )
    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    init { refresh() }

    private fun refresh() = viewModelScope.launch {
        val now = System.currentTimeMillis()
        val weekStart = now - 7*86_400_000L
        val monthStart = now - 30*86_400_000L
        val week = repo.spendBetween(weekStart, now)
        val month = repo.spendBetween(monthStart, now)
        _ui.update { it.copy(weekTotal = week.total, monthTotal = month.total, topItems = month.byItem.take(10)) }
    }
}
