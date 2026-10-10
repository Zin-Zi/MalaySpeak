package com.malayspeak.app.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.malayspeak.app.data.local.dao.LearningProgressDao
import com.malayspeak.app.data.local.dao.VocabularyCardDao
import com.malayspeak.app.data.local.entity.LearningProgress
import com.malayspeak.app.data.local.entity.VocabularyCard
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.io.BufferedReader
import java.io.InputStreamReader

@Database(
    entities = [VocabularyCard::class, LearningProgress::class],
    version = 6,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun vocabularyCardDao(): VocabularyCardDao
    abstract fun learningProgressDao(): LearningProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        private val MIGRATION_4_5 = object : Migration(4, 5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DELETE FROM vocabulary_cards WHERE malayPhrase LIKE 'Frasa harian praktikal%' OR malayPhrase = '' OR malayPhrase IS NULL")
            }
        }

        private val MIGRATION_5_6 = object : Migration(5, 6) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Clear obsolete entries and placeholder rows to replace with new JSON dataset
                database.execSQL("DELETE FROM vocabulary_cards WHERE malayPhrase LIKE 'Frasa harian praktikal%' OR malayPhrase = '' OR malayPhrase IS NULL")
                database.execSQL("DELETE FROM vocabulary_cards")
                database.execSQL("DELETE FROM learning_progress")
            }
        }

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val appContext = context.applicationContext
                val instance = Room.databaseBuilder(
                    appContext,
                    AppDatabase::class.java,
                    "malayspeak_offline.db"
                )
                    .addMigrations(MIGRATION_4_5, MIGRATION_5_6)
                    .addCallback(DatabasePrepopulateCallback(appContext))
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabasePrepopulateCallback(private val context: Context) : Callback() {
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                db.execSQL("DELETE FROM vocabulary_cards WHERE malayPhrase LIKE 'Frasa harian praktikal%' OR malayPhrase = '' OR malayPhrase IS NULL")
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val currentCount = database.vocabularyCardDao().getDirectCount()
                        val phrases = loadPhrasesFromAssets(context)
                        if (currentCount < phrases.size) {
                            database.vocabularyCardDao().insertAll(phrases)
                            database.learningProgressDao().saveProgress(
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
            }

            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val phrases = loadPhrasesFromAssets(context)
                        if (phrases.isNotEmpty()) {
                            database.vocabularyCardDao().insertAll(phrases)
                            database.learningProgressDao().saveProgress(
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
            }
        }

        fun loadPhrasesFromAssets(context: Context): List<VocabularyCard> {
            val list = mutableListOf<VocabularyCard>()
            try {
                val inputStream = context.assets.open("phrases.json")
                val reader = BufferedReader(InputStreamReader(inputStream, "UTF-8"))
                val content = reader.use { it.readText() }
                val jsonArray = JSONArray(content)

                for (i in 0 until jsonArray.length()) {
                    val obj = jsonArray.getJSONObject(i)
                    val category = obj.optString("category", "GREETINGS")
                    val categoryMm = obj.optString("categoryMm", "")
                    val malay = obj.optString("malay", "")
                    val english = obj.optString("english", "")
                    val myanmar = obj.optString("myanmar", "")
                    val explanation = obj.optString("explanation", "")

                    list.add(
                        VocabularyCard(
                            id = (i + 1).toLong(),
                            category = category,
                            categoryMm = categoryMm,
                            malayPhrase = malay,
                            malayPronunciation = "",
                            myanmarMeaning = myanmar,
                            englishMeaning = english,
                            culturalContext = explanation,
                            isLearned = false,
                            learnedTimestamp = null
                        )
                    )
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return list.filterNot { 
                it.malayPhrase.startsWith("Frasa harian praktikal") || 
                it.malayPhrase.isBlank() 
            }
        }
    }
}
