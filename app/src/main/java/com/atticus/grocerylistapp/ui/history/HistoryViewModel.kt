package com.atticus.grocerylistapp.ui.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atticus.grocerylistapp.repo.GroceryRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val repo: GroceryRepo
) : ViewModel() {

    data class UiItem(
        val itemId: Long,
        val name: String,
        val type: String,           // "bought" | "oos"
        val price: Double?
    )

    data class UiState(
        val selectedDayStart: Long = startOfDay(System.currentTimeMillis()),
        val items: List<UiItem> = emptyList(),
        val totalBought: Double = 0.0
    )
    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    init { loadDay(_ui.value.selectedDayStart) }

    fun onDayChange(dayStartUtcMs: Long) {
        _ui.update { it.copy(selectedDayStart = dayStartUtcMs) }
        loadDay(dayStartUtcMs)
    }

    fun addOne(itemId: Long) = viewModelScope.launch { repo.addPlanned(itemId) }

    private fun loadDay(dayStart: Long) = viewModelScope.launch {
        val dayEnd = dayStart + 86_400_000L
        val entries = repo.historyEntriesInDay(dayStart, dayEnd)
        val uiItems = entries.map {
            UiItem(itemId = it.itemId, name = it.name, type = it.type, price = it.priceTotal)
        }
        val total = entries.filter { it.type == "bought" }.sumOf { it.priceTotal ?: 0.0 }
        _ui.update { it.copy(items = uiItems, totalBought = total) }
    }

    companion object {
        fun startOfDay(ts: Long): Long {
            val cal = Calendar.getInstance()
            cal.timeInMillis = ts
            cal.set(Calendar.HOUR_OF_DAY, 0)
            cal.set(Calendar.MINUTE, 0)
            cal.set(Calendar.SECOND, 0)
            cal.set(Calendar.MILLISECOND, 0)
            return cal.timeInMillis
        }
    }
}
