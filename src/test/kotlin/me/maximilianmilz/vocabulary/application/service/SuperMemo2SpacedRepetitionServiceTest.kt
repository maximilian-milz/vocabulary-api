package me.maximilianmilz.vocabulary.application.service

import me.maximilianmilz.vocabulary.domain.model.Category
import me.maximilianmilz.vocabulary.domain.model.VocabularyEntry
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import java.time.LocalDate
import java.time.LocalDateTime
import kotlin.math.roundToInt

class SuperMemo2SpacedRepetitionServiceTest {

    private lateinit var service: SuperMemo2SpacedRepetitionService
    private lateinit var testEntry: VocabularyEntry

    @BeforeEach
    fun setUp() {
        service = SuperMemo2SpacedRepetitionService()
        
        // Create a test vocabulary entry
        testEntry = VocabularyEntry(
            id = 1L,
            wordPt = "falar",
            wordDe = "sprechen",
            example = "Eu falo português.",
            level = 1,
            nextReview = LocalDate.now(),
            category = Category.VERBS,
            createdAt = LocalDateTime.now(),
            repetitions = 0,
            easeFactor = SuperMemo2SpacedRepetitionService.DEFAULT_EASE_FACTOR,
            lastReviewDate = null
        )
    }

    @Test
    fun `test calculateNextReviewDate with zero repetitions`() {
        // When repetitions is 0, the next review date should be 1 day from now
        val nextReviewDate = service.calculateNextReviewDate(0, 2.5, 0)
        
        assertEquals(LocalDate.now().plusDays(1), nextReviewDate)
    }

    @Test
    fun `test calculateNextReviewDate with one repetition`() {
        // When repetitions is 1, the next review date should be 6 days from now
        val nextReviewDate = service.calculateNextReviewDate(1, 2.5, 0)
        
        assertEquals(LocalDate.now().plusDays(6), nextReviewDate)
    }

    @Test
    fun `test calculateNextReviewDate with multiple repetitions`() {
        // When repetitions > 1, the next review date should be calculated using the formula
        val easeFactor = 2.5
        val previousInterval = 6
        val expectedInterval = (previousInterval * easeFactor).roundToInt()
        
        val nextReviewDate = service.calculateNextReviewDate(2, easeFactor, previousInterval)
        
        assertEquals(LocalDate.now().plusDays(expectedInterval.toLong()), nextReviewDate)
    }

    @ParameterizedTest
    @ValueSource(ints = [0, 1, 2])
    fun `test processReviewResult with quality rating less than 3`(qualityRating: Int) {
        // When quality rating is less than 3, repetitions should be reset to 0
        val result = service.processReviewResult(testEntry, qualityRating)
        
        assertEquals(0, result.repetitions)
        assertEquals(testEntry.easeFactor, result.easeFactor) // Ease factor should not change
        assertEquals(LocalDate.now().plusDays(1), result.nextReview) // Next review should be tomorrow
        assertEquals(LocalDate.now(), result.lastReviewDate) // Last review date should be today
    }

    @ParameterizedTest
    @ValueSource(ints = [3, 4, 5])
    fun `test processReviewResult with quality rating 3 or higher`(qualityRating: Int) {
        // When quality rating is 3 or higher, repetitions should increase
        val result = service.processReviewResult(testEntry, qualityRating)
        
        assertEquals(testEntry.repetitions!! + 1, result.repetitions)
        assertNotNull(result.easeFactor)
        assertEquals(LocalDate.now(), result.lastReviewDate) // Last review date should be today
        
        // For first successful review, next review should be tomorrow
        assertEquals(LocalDate.now().plusDays(1), result.nextReview)
    }

    @Test
    fun `test processReviewResult with quality rating 5 increases ease factor`() {
        // When quality rating is 5, ease factor should increase
        val initialEaseFactor = testEntry.easeFactor!!
        val result = service.processReviewResult(testEntry, 5)
        
        assertTrue(result.easeFactor!! > initialEaseFactor)
    }

    @Test
    fun `test processReviewResult with quality rating 3 decreases ease factor`() {
        // When quality rating is 3, ease factor should decrease
        val initialEaseFactor = testEntry.easeFactor!!
        val result = service.processReviewResult(testEntry, 3)
        
        assertTrue(result.easeFactor!! < initialEaseFactor)
    }

    @Test
    fun `test processReviewResult with multiple successful reviews`() {
        // Simulate multiple successful reviews and check the progression
        var entry = testEntry
        
        // First review (quality 4)
        entry = service.processReviewResult(entry, 4)
        assertEquals(1, entry.repetitions)
        assertEquals(LocalDate.now().plusDays(1), entry.nextReview)
        
        // Second review (quality 4)
        val entryWithLastReview = entry.copy(lastReviewDate = LocalDate.now().minusDays(1))
        entry = service.processReviewResult(entryWithLastReview, 4)
        assertEquals(2, entry.repetitions)
        assertEquals(LocalDate.now().plusDays(6), entry.nextReview)
        
        // Third review (quality 4)
        val entryWithSecondReview = entry.copy(lastReviewDate = LocalDate.now().minusDays(6))
        entry = service.processReviewResult(entryWithSecondReview, 4)
        assertEquals(3, entry.repetitions)
        
        // The interval should now be 6 * easeFactor
        val expectedInterval = (6 * entry.easeFactor!!).roundToInt()
        assertEquals(LocalDate.now().plusDays(expectedInterval.toLong()), entry.nextReview)
    }

    @Test
    fun `test processReviewResult with quality rating out of range`() {
        // Test with quality rating below 0 (should be coerced to 0)
        val resultWithNegative = service.processReviewResult(testEntry, -1)
        assertEquals(0, resultWithNegative.repetitions)
        
        // Test with quality rating above 5 (should be coerced to 5)
        val resultWithTooHigh = service.processReviewResult(testEntry, 6)
        assertEquals(1, resultWithTooHigh.repetitions)
    }

    @Test
    fun `test ease factor never goes below minimum`() {
        // Create an entry with ease factor close to minimum
        val entryWithLowEaseFactor = testEntry.copy(
            easeFactor = SuperMemo2SpacedRepetitionService.MIN_EASE_FACTOR + 0.1,
            repetitions = 1
        )
        
        // Process with low quality (3) to decrease ease factor
        val result = service.processReviewResult(entryWithLowEaseFactor, 3)
        
        // Ease factor should not go below minimum
        assertTrue(result.easeFactor!! >= SuperMemo2SpacedRepetitionService.MIN_EASE_FACTOR)
    }
}