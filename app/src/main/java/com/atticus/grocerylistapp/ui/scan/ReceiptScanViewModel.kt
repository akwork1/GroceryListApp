package com.atticus.grocerylistapp.ui.scan

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
class ReceiptScanViewModel @Inject constructor(
    private val repo: GroceryRepo
): ViewModel() {

    data class UiState(val rawText: String = "", val status: String = "")
    private val _ui = MutableStateFlow(UiState())
    val ui: StateFlow<UiState> = _ui

    fun onRawChange(t:String){ _ui.update{ it.copy(rawText = t) } }

    fun onIngest(store:String?) = viewModelScope.launch {
        val lines = _ui.value.rawText.lines().filter { it.isNotBlank() }
        repo.ingestReceiptLines(lines, store)
        _ui.update { it.copy(status = "Ingested ${lines.size} lines") }
    }
}
