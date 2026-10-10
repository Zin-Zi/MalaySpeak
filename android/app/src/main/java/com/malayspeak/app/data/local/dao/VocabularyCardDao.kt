package com.malayspeak.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.malayspeak.app.data.local.entity.VocabularyCard
import kotlinx.coroutines.flow.Flow

@Dao
interface VocabularyCardDao {

    @Query("SELECT * FROM vocabulary_cards ORDER BY id ASC")
    fun getAllCards(): Flow<List<VocabularyCard>>

    @Query("SELECT * FROM vocabulary_cards WHERE category = :category ORDER BY id ASC")
    fun getCardsByCategory(category: String): Flow<List<VocabularyCard>>

    @Query("""
        SELECT * FROM vocabulary_cards 
        WHERE malayPhrase LIKE '%' || :query || '%' 
           OR englishMeaning LIKE '%' || :query || '%' 
           OR myanmarMeaning LIKE '%' || :query || '%'
           OR malayPronunciation LIKE '%' || :query || '%'
        ORDER BY id ASC
    """)
    fun searchPhrases(query: String): Flow<List<VocabularyCard>>

    @Query("SELECT * FROM vocabulary_cards WHERE isLearned = 1 ORDER BY learnedTimestamp DESC")
    fun getLearnedCards(): Flow<List<VocabularyCard>>

    @Query("SELECT * FROM vocabulary_cards WHERE isLearned = 0 ORDER BY id ASC")
    fun getUnlearnedCards(): Flow<List<VocabularyCard>>

    @Query("SELECT COUNT(*) FROM vocabulary_cards")
    fun getTotalCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM vocabulary_cards WHERE isLearned = 1")
    fun getLearnedCount(): Flow<Int>

    @Query("SELECT COUNT(*) FROM vocabulary_cards WHERE category = :category")
    fun getTotalCountByCategory(category: String): Flow<Int>

    @Query("SELECT COUNT(*) FROM vocabulary_cards WHERE category = :category AND isLearned = 1")
    fun getLearnedCountByCategory(category: String): Flow<Int>

    @Query("SELECT DISTINCT category FROM vocabulary_cards")
    fun getAllCategories(): Flow<List<String>>

    @Query("UPDATE vocabulary_cards SET isLearned = :isLearned, learnedTimestamp = :timestamp WHERE id = :id")
    suspend fun updateLearnedStatus(id: Long, isLearned: Boolean, timestamp: Long?)

    @Query("UPDATE vocabulary_cards SET isLearned = 0, learnedTimestamp = NULL")
    suspend fun resetAllProgress()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(cards: List<VocabularyCard>)

    @Query("SELECT COUNT(*) FROM vocabulary_cards")
    suspend fun getDirectCount(): Int

    @Query("DELETE FROM vocabulary_cards WHERE malayPhrase LIKE 'Frasa harian praktikal%' OR malayPhrase = '' OR malayPhrase IS NULL")
    suspend fun deletePlaceholderCards(): Int

    @Query("DELETE FROM vocabulary_cards")
    suspend fun clearAllCards()
}
