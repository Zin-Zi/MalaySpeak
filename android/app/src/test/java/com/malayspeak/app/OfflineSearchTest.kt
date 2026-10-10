package com.malayspeak.app

import com.malayspeak.app.data.local.seed.PhraseSeedData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class OfflineSearchTest {

    @Test
    fun testSeedDataContains500Phrases() {
        val phrases = PhraseSeedData.getInitial500Phrases()
        assertEquals(500, phrases.size)
    }

    @Test
    fun testOfflineSearchByMalayKeyword() {
        val phrases = PhraseSeedData.getInitial500Phrases()
        val query = "berapa"
        val results = phrases.filter {
            it.malayPhrase.contains(query, ignoreCase = true)
        }
        assertTrue("Should find phrases with 'berapa'", results.isNotEmpty())
    }

    @Test
    fun testOfflineSearchByEnglishKeyword() {
        val phrases = PhraseSeedData.getInitial500Phrases()
        val query = "discount"
        val results = phrases.filter {
            it.englishMeaning.contains(query, ignoreCase = true)
        }
        assertTrue("Should find phrases mentioning discount", results.isNotEmpty())
    }

    @Test
    fun testOfflineSearchByMyanmarKeyword() {
        val phrases = PhraseSeedData.getInitial500Phrases()
        val query = "ဘယ်လောက်"
        val results = phrases.filter {
            it.myanmarMeaning.contains(query)
        }
        assertTrue("Should find phrases with Burmese keyword", results.isNotEmpty())
    }

    @Test
    fun testAllCategoriesArePopulated() {
        val phrases = PhraseSeedData.getInitial500Phrases()
        val expectedCategories = setOf(
            "GREETINGS", "MARKET", "DINING", "TRANSPORT", "WORK",
            "HOUSING", "NUMBERS_TIME", "HEALTH_EMERGENCY", "DAILY_LIFE", "SLANG_PARTICLES"
        )
        val actualCategories = phrases.map { it.category }.toSet()
        assertEquals(expectedCategories, actualCategories)
    }
}
