package com.malayspeak.app.ui.screens.vocabulary

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malayspeak.app.data.local.entity.VocabularyCard
import com.malayspeak.app.data.repository.OfflinePhraseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class VocabularyUiState(
    val searchQuery: String = "",
    val deck: List<VocabularyCard> = emptyList(),
    val currentIndex: Int = 0,
    val selectedCategory: String = "ALL",
    val isFlipped: Boolean = false,
    val isLoading: Boolean = true
)

class VocabularyViewModel(
    private val repository: OfflinePhraseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(VocabularyUiState())
    val uiState: StateFlow<VocabularyUiState> = _uiState.asStateFlow()

    init {
        loadDeck("ALL")
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        loadDeck(_uiState.value.selectedCategory)
    }

    fun loadDeck(category: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, selectedCategory = category) }
            val query = _uiState.value.searchQuery.trim()

            if (query.isNotEmpty()) {
                repository.searchPhrases(query).take(1).collect { cards ->
                    val filtered = if (category == "ALL") cards else cards.filter { it.category == category }
                    _uiState.update {
                        it.copy(
                            deck = filtered,
                            currentIndex = 0,
                            isFlipped = false,
                            isLoading = false
                        )
                    }
                }
            } else {
                val flow = if (category == "ALL") {
                    repository.getAllCards()
                } else {
                    repository.getCardsByCategory(category)
                }
                flow.take(1).collect { cards ->
                    _uiState.update {
                        it.copy(
                            deck = cards,
                            currentIndex = 0,
                            isFlipped = false,
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun nextCard() {
        _uiState.update { state ->
            if (state.deck.isEmpty()) state
            else {
                val nextIdx = (state.currentIndex + 1) % state.deck.size
                state.copy(currentIndex = nextIdx, isFlipped = false)
            }
        }
    }

    fun previousCard() {
        _uiState.update { state ->
            if (state.deck.isEmpty()) state
            else {
                val prevIdx = if (state.currentIndex > 0) state.currentIndex - 1 else state.deck.size - 1
                state.copy(currentIndex = prevIdx, isFlipped = false)
            }
        }
    }

    fun toggleFlip() {
        _uiState.update { it.copy(isFlipped = !it.isFlipped) }
    }

    fun toggleLearnedCurrentCard() {
        val currentCard = currentCard() ?: return
        viewModelScope.launch {
            repository.updateCardLearned(currentCard.id, !currentCard.isLearned)
        }
    }

    fun toggleCardLearned(cardId: Long, currentLearned: Boolean) {
        viewModelScope.launch {
            repository.updateCardLearned(cardId, !currentLearned)
        }
    }

    fun currentCard(): VocabularyCard? {
        val state = _uiState.value
        return state.deck.getOrNull(state.currentIndex)
    }
}
