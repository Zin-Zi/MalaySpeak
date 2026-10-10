package com.malayspeak.app.data.repository

import android.content.Context
import com.malayspeak.app.data.local.AppDatabase
import com.malayspeak.app.data.local.dao.LearningProgressDao
import com.malayspeak.app.data.local.dao.VocabularyCardDao
import com.malayspeak.app.data.local.entity.LearningProgress
import com.malayspeak.app.data.local.entity.VocabularyCard
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class OfflinePhraseRepository(
    private val cardDao: VocabularyCardDao,
    private val progressDao: LearningProgressDao,
    private val context: Context? = null
) {
    fun getAllCards(): Flow<List<VocabularyCard>> = cardDao.getAllCards()

    fun getCardsByCategory(category: String): Flow<List<VocabularyCard>> =
        cardDao.getCardsByCategory(category)

    fun searchPhrases(query: String): Flow<List<VocabularyCard>> =
        cardDao.searchPhrases(query)

    fun getLearnedCards(): Flow<List<VocabularyCard>> = cardDao.getLearnedCards()

    fun getTotalCount(): Flow<Int> = cardDao.getTotalCount()

    fun getLearnedCount(): Flow<Int> = cardDao.getLearnedCount()

    fun getProgress(): Flow<LearningProgress?> = progressDao.getProgress()

    suspend fun updateCardLearned(cardId: Long, isLearned: Boolean) = withContext(Dispatchers.IO) {
        val timestamp = if (isLearned) System.currentTimeMillis() else null
        cardDao.updateLearnedStatus(cardId, isLearned, timestamp)
    }

    suspend fun resetAllProgress() = withContext(Dispatchers.IO) {
        cardDao.resetAllProgress()
        progressDao.resetProgress()
    }

    suspend fun clearPlaceholdersAndRefreshSeed() = withContext(Dispatchers.IO) {
        cardDao.deletePlaceholderCards()
        val currentCount = cardDao.getDirectCount()
        if (context != null) {
            val phrases = AppDatabase.loadPhrasesFromAssets(context)
            if (phrases.isNotEmpty() && (currentCount == 0 || currentCount < phrases.size)) {
                cardDao.insertAll(phrases)
                progressDao.saveProgress(
                    LearningProgress(
                        id = 1,
                        totalPhrases = phrases.size,
                        learnedCount = 0,
                        dailyStreakDays = 1,
                        lastStudyTimestamp = System.currentTimeMillis()
                    )
                )
            }
        }
    }

    suspend fun ensureDatabaseSeeded() = withContext(Dispatchers.IO) {
        clearPlaceholdersAndRefreshSeed()
    }
}
