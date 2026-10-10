package com.malayspeak.app

import com.malayspeak.app.data.local.entity.VocabularyCard
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class VocabularyDaoTest {

    @Test
    fun testCardLearnedToggleLogic() {
        val originalCard = VocabularyCard(
            id = 1,
            category = "GREETINGS",
            categoryMm = "နှုတ်ဆက်ခြင်း",
            malayPhrase = "Apa khabar?",
            malayPronunciation = "အာပါ ခါဘာရ်?",
            myanmarMeaning = "နေကောင်းလား။",
            englishMeaning = "How are you?",
            culturalContext = "Standard greeting.",
            isLearned = false
        )

        assertFalse(originalCard.isLearned)

        val updatedCard = originalCard.copy(
            isLearned = true,
            learnedTimestamp = System.currentTimeMillis()
        )

        assertTrue(updatedCard.isLearned)
        assertTrue(updatedCard.learnedTimestamp != null)
    }

    @Test
    fun testCategoryFilterLogic() {
        val cards = listOf(
            VocabularyCard(1, "GREETINGS", "နှုတ်ဆက်ခြင်း", "Hai", "ဟိုင်း", "ဟိုင်း", "Hi", "Hi", false),
            VocabularyCard(2, "MARKET", "ဈေးဝယ်ခြင်း", "Berapa", "ဘရာပါ", "ဘယ်လောက်", "How much", "How much", false),
            VocabularyCard(3, "DINING", "စားသောက်ဆိုင်", "Makan", "မာကန်", "စား", "Eat", "Eat", false)
        )

        val marketCards = cards.filter { it.category == "MARKET" }
        assertEquals(1, marketCards.size)
        assertEquals("Berapa", marketCards.first().malayPhrase)
    }
}
