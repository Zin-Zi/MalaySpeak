package com.malayspeak.app

import android.app.Application
import com.malayspeak.app.data.local.AppDatabase
import com.malayspeak.app.data.repository.OfflinePhraseRepository

/**
 * Main Application class for မလေးစကားပြော (MalaySpeak Offline).
 * Holds app-wide singletons including Room Database instance and repository.
 */
class MalaySpeakApp : Application() {

    val database by lazy { AppDatabase.getInstance(this) }
    val repository by lazy { OfflinePhraseRepository(database.vocabularyCardDao(), database.learningProgressDao()) }
    val themePreferencesRepository by lazy { com.malayspeak.app.data.local.preferences.ThemePreferencesRepository(this) }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: MalaySpeakApp
            private set
    }
}
