package me.maximilianmilz.vocabulary.application.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.maximilianmilz.vocabulary.domain.model.Category
import me.maximilianmilz.vocabulary.domain.model.ReviewHistory
import me.maximilianmilz.vocabulary.domain.model.VocabularyEntry
import me.maximilianmilz.vocabulary.domain.repository.ReviewHistoryRepository
import me.maximilianmilz.vocabulary.domain.repository.ReviewSessionRepository
import me.maximilianmilz.vocabulary.domain.repository.VocabularyEntryRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDate
import java.time.LocalDateTime

class ReviewServiceImplTest {

    private lateinit var reviewService: ReviewServiceImpl
    private lateinit var vocabularyEntryRepository: VocabularyEntryRepository
    private lateinit var reviewHistoryRepository: ReviewHistoryRepository
    private lateinit var reviewSessionRepository: ReviewSessionRepository
    private lateinit var spacedRepetitionService: SpacedRepetitionService

    @BeforeEach
    fun setUp() {
        vocabularyEntryRepository = mockk(relaxed = true)
        reviewHistoryRepository = mockk(relaxed = true)
        reviewSessionRepository = mockk(relaxed = true)
        spacedRepetitionService = mockk(relaxed = true)

        reviewService = ReviewServiceImpl(
            vocabularyEntryRepository,
            reviewHistoryRepository,
            reviewSessionRepository,
            spacedRepetitionService
        )
    }

    @Test
    fun `getEntriesDueForReview returns entries due for review`() {
        // Given
        val today = LocalDate.now()
        val entry1 = createVocabularyEntry(1, today.minusDays(1))
        val entry2 = createVocabularyEntry(2, today.minusDays(2))
        val entries = listOf(entry1, entry2)

        every { vocabularyEntryRepository.findByNextReviewBefore(today) } returns entries

        // When
        val result = reviewService.getEntriesDueForReview()

        // Then
        assertEquals(entries, result)
        verify { vocabularyEntryRepository.findByNextReviewBefore(today) }
    }

    @Test
    fun `getEntriesDueForReview with limit returns limited entries`() {
        // Given
        val today = LocalDate.now()
        val entry1 = createVocabularyEntry(1, today.minusDays(1))
        val entry2 = createVocabularyEntry(2, today.minusDays(2))
        val entries = listOf(entry1, entry2)

        every { vocabularyEntryRepository.findByNextReviewBefore(today) } returns entries

        // When
        val result = reviewService.getEntriesDueForReview(1)

        // Then
        assertEquals(listOf(entry1), result)
        verify { vocabularyEntryRepository.findByNextReviewBefore(today) }
    }

    @Test
    fun `recordReviewResult updates entry and creates history`() {
        // Given
        val entryId = 1L
        val qualityRating = 4
        val notes = "Good recall"
        val entry = createVocabularyEntry(entryId, LocalDate.now())
        val updatedEntry = entry.copy(level = 2)

        every { vocabularyEntryRepository.findById(entryId) } returns entry
        every { spacedRepetitionService.processReviewResult(entry, qualityRating) } returns updatedEntry
        every { vocabularyEntryRepository.save(updatedEntry) } returns updatedEntry
        every { reviewHistoryRepository.save(any()) } answers { firstArg() }

        // When
        val result = reviewService.recordReviewResult(entryId, qualityRating, notes)

        // Then
        assertEquals(updatedEntry, result)
        verify { vocabularyEntryRepository.findById(entryId) }
        verify { spacedRepetitionService.processReviewResult(entry, qualityRating) }
        verify { vocabularyEntryRepository.save(updatedEntry) }
        verify { reviewHistoryRepository.save(match { 
            it.vocabularyEntryId == entryId && 
            it.qualityRating == qualityRating && 
            it.notes == notes 
        }) }
    }

    @Test
    fun `recordReviewResult throws exception when entry not found`() {
        // Given
        val entryId = 1L
        val qualityRating = 4

        every { vocabularyEntryRepository.findById(entryId) } returns null

        // When/Then
        assertThrows<IllegalArgumentException> {
            reviewService.recordReviewResult(entryId, qualityRating)
        }
    }

    @Test
    fun `getReviewHistory returns history for entry`() {
        // Given
        val entryId = 1L
        val entry = createVocabularyEntry(entryId, LocalDate.now())
        val history = listOf(
            ReviewHistory(
                id = 1L,
                vocabularyEntryId = entryId,
                reviewDate = LocalDate.now(),
                qualityRating = 4,
                createdAt = LocalDateTime.now()
            )
        )

        every { vocabularyEntryRepository.findById(entryId) } returns entry
        every { reviewHistoryRepository.findByVocabularyEntryId(entryId) } returns history

        // When
        val result = reviewService.getReviewHistory(entryId)

        // Then
        assertEquals(history, result)
        verify { vocabularyEntryRepository.findById(entryId) }
        verify { reviewHistoryRepository.findByVocabularyEntryId(entryId) }
    }

    @Test
    fun `getReviewHistory throws exception when entry not found`() {
        // Given
        val entryId = 1L

        every { vocabularyEntryRepository.findById(entryId) } returns null

        // When/Then
        assertThrows<IllegalArgumentException> {
            reviewService.getReviewHistory(entryId)
        }
    }

    private fun createVocabularyEntry(id: Long, nextReview: LocalDate): VocabularyEntry {
        return VocabularyEntry(
            id = id,
            wordPt = "palavra$id",
            wordDe = "wort$id",
            example = "Example $id",
            level = 1,
            nextReview = nextReview,
            category = Category.NOUNS,
            createdAt = LocalDateTime.now()
        )
    }
}