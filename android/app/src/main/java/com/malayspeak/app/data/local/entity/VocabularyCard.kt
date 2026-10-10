package com.malayspeak.app.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Database entity representing a conversational Malay phrase card.
 * Indexed on search fields to provide instant offline tri-lingual query lookup.
 */
@Entity(
    tableName = "vocabulary_cards",
    indices = [
        Index(value = ["category"]),
        Index(value = ["malayPhrase"]),
        Index(value = ["englishMeaning"]),
        Index(value = ["myanmarMeaning"]),
        Index(value = ["isLearned"])
    ]
)
data class VocabularyCard(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val category: String,           // e.g. "GREETINGS", "MARKET", "DINING", "TRANSPORT", "WORK", "EMERGENCY"
    val categoryMm: String,         // e.g. "နှုတ်ဆက်ခြင်း", "ဈေးဝယ်ခြင်း", "စားသောက်ဆိုင်"
    val malayPhrase: String,        // e.g. "Berapa ni boss?"
    val malayPronunciation: String, // Phonetic guide in Burmese script: "ဘရာပါ နီ ဘော့စ်?"
    val myanmarMeaning: String,     // Burmese translation: "ဒါ ဘယ်လောက်လဲ အကို/သူဌေး။"
    val englishMeaning: String,     // English translation: "How much is this, boss?"
    val culturalContext: String,    // Colloquial Malaysian usage notes
    val isLearned: Boolean = false, // Mastery flag
    val learnedTimestamp: Long? = null
)
