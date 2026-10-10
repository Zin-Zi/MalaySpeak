package com.malayspeak.app.ui.screens.progress

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.malayspeak.app.data.local.entity.VocabularyCard
import com.malayspeak.app.data.repository.OfflinePhraseRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CategoryProgressItem(
    val category: String,
    val categoryMm: String,
    val totalCount: Int,
    val learnedCount: Int,
    val percentage: Int
)

data class ProgressUiState(
    val totalCount: Int = 500,
    val learnedCount: Int = 0,
    val progressPercentage: Int = 0,
    val categoriesProgress: List<CategoryProgressItem> = emptyList(),
    val recentlyLearned: List<VocabularyCard> = emptyList(),
    val isLoading: Boolean = false
)

class ProgressViewModel(
    private val repository: OfflinePhraseRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProgressUiState())
    val uiState: StateFlow<ProgressUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                repository.getAllCards(),
                repository.getLearnedCards()
            ) { allCards, learnedCards ->
                val total = allCards.size
                val learned = learnedCards.size
                val percentage = if (total > 0) (learned * 100) / total else 0

                val categoryGroups = allCards.groupBy { it.category }
                val catProgress = categoryGroups.map { (cat, cards) ->
                    val catLearned = cards.count { it.isLearned }
                    val catPct = if (cards.isNotEmpty()) (catLearned * 100) / cards.size else 0
                    val catMm = cards.firstOrNull()?.categoryMm ?: cat
                    CategoryProgressItem(
                        category = cat,
                        categoryMm = catMm,
                        totalCount = cards.size,
                        learnedCount = catLearned,
                        percentage = catPct
                    )
                }

                ProgressUiState(
                    totalCount = total,
                    learnedCount = learned,
                    progressPercentage = percentage,
                    categoriesProgress = catProgress,
                    recentlyLearned = learnedCards.take(15)
                )
            }.collect { state ->
                _uiState.value = state
            }
        }
    }

    fun resetProgress() {
        viewModelScope.launch {
            repository.resetAllProgress()
        }
    }
}
