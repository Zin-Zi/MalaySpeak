package com.malayspeak.app.ui.screens.translate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malayspeak.app.data.local.entity.VocabularyCard
import com.malayspeak.app.data.repository.OfflinePhraseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class TranslationUiState(
    val searchQuery: String = "",
    val searchResults: List<VocabularyCard> = emptyList(),
    val selectedCategory: String = "ALL",
    val isLoading: Boolean = false
)

class TranslationViewModel(
    private val repository: OfflinePhraseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TranslationUiState())
    val uiState: StateFlow<TranslationUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.ensureDatabaseSeeded()
            loadPhrases()
        }
    }

    private fun loadPhrases() {
        viewModelScope.launch {
            val query = _uiState.value.searchQuery.trim()
            val category = _uiState.value.selectedCategory

            if (query.isNotEmpty()) {
                repository.searchPhrases(query).take(1).collect { results ->
                    val filtered = if (category == "ALL") results else results.filter { it.category == category }
                    _uiState.update { it.copy(searchResults = filtered) }
                }
            } else if (category == "ALL") {
                repository.getAllCards().take(1).collect { cards ->
                    _uiState.update { it.copy(searchResults = cards) }
                }
            } else {
                repository.getCardsByCategory(category).take(1).collect { cards ->
                    _uiState.update { it.copy(searchResults = cards) }
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadPhrases()
    }

    fun onCategorySelected(category: String) {
        _uiState.update { it.copy(selectedCategory = category) }
        loadPhrases()
    }
}
