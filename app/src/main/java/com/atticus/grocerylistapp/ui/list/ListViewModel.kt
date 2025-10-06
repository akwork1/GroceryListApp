package com.atticus.grocerylistapp.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.atticus.grocerylistapp.data.dao.PlannedWithItemName
import com.atticus.grocerylistapp.repo.GroceryRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListViewModel @Inject constructor(
    private val repo: GroceryRepo
): ViewModel() {

    data class UiState(
        val query: String = "",
        val matches: List<String> = emptyList(),
        val current: List<PlannedWithItemName> = emptyList()
    )
    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    init { refresh() }

    private fun refresh() = viewModelScope.launch {
        _ui.update { it.copy(current = repo.currentList()) }
    }

    fun onQuery(q: String) {
        _ui.update { it.copy(query = q) }
        updateMatches()
    }

    private fun updateMatches() = viewModelScope.launch {
        val q = _ui.value.query.trim()
        val names = if (q.isBlank()) emptyList() else repo.searchPantryDisplayNames(q)
        _ui.update { it.copy(matches = names.take(8)) }
    }

    fun addManualOrSelected(name: String? = null) = viewModelScope.launch {
        val text = name ?: _ui.value.query
        if (text.isNotBlank()) {
            repo.addManual(text)
            _ui.update { it.copy(query = "", matches = emptyList()) }
            refresh()
        }
    }

    fun markBought(id: Long)   = viewModelScope.launch { repo.markBought(id); refresh() }
    fun markOOS(id: Long)      = viewModelScope.launch { repo.markOutOfStock(id); refresh() }
    fun removeLine(id: Long)   = viewModelScope.launch { repo.removePlanned(id); refresh() }
    fun clearDone()            = viewModelScope.launch { repo.clearCompleted(); refresh() }
}
