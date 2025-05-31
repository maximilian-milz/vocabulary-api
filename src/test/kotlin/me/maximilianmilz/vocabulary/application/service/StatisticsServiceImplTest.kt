package me.maximilianmilz.vocabulary.application.service

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import me.maximilianmilz.vocabulary.domain.model.Category
import me.maximilianmilz.vocabulary.domain.model.ReviewHistory
import me.maximilianmilz.vocabulary.domain.model.ReviewSessionStatus
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

class StatisticsServiceImplTest {

    private lateinit var statisticsService: StatisticsServiceImpl
    private lateinit var vocabularyEntryRepository: VocabularyEntryRepository
    private lateinit var reviewHistoryRepository: ReviewHistoryRepository
    private lateinit var reviewSessionRepository: ReviewSessionRepository

    @BeforeEach
    fun setUp() {
        vocabularyEntryRepository = mockk(relaxed = true)
        reviewHistoryRepository = mockk(relaxed = true)
        reviewSessionRepository = mockk(relaxed = true)

        statisticsService = StatisticsServiceImpl(
            vocabularyEntryRepository,
            reviewHistoryRepository,
            reviewSessionRepository
        )
    }

    @Test
    fun `getOverallStatistics returns correct statistics`() {
        // Given
        val entries = listOf(
            createVocabularyEntry(1, LocalDate.now(), 1),
            createVocabularyEntry(2, LocalDate.now(), 2),
            createVocabularyEntry(3, LocalDate.now().plusDays(1), 3)
        )
        val entriesDueToday = entries.filter { it.nextReview <= LocalDate.now() }
        val entriesDueTomorrow = entries.filter { 
            it.nextReview.isAfter(LocalDate.now()) && 
            it.nextReview <= LocalDate.now().plusDays(2) 
        }
        val reviewHistory = listOf(
            ReviewHistory(
                id = 1L,
                vocabularyEntryId = 1L,
                reviewDate = LocalDate.now(),
                qualityRating = 4,
                createdAt = LocalDateTime.now()
            )
        )

        every { vocabularyEntryRepository.findAll() } returns entries
        every { vocabularyEntryRepository.findByNextReviewBefore(LocalDate.now()) } returns entriesDueToday
        every { vocabularyEntryRepository.findByNextReviewBefore(LocalDate.now().plusDays(2)) } returns entriesDueToday + entriesDueTomorrow
        every { reviewHistoryRepository.findAll() } returns reviewHistory
        every { reviewSessionRepository.findByStatus(ReviewSessionStatus.COMPLETED) } returns emptyList()

        // When
        val result = statisticsService.getOverallStatistics()

        // Then
        assertEquals(entries.size, result["totalEntries"])
        assertEquals(entries.map { it.level }.average(), result["averageLevel"])
        assertEquals(entriesDueToday.size, result["entriesDueToday"])
        assertEquals(entriesDueTomorrow.size, result["entriesDueTomorrow"])
        assertEquals(reviewHistory.size, result["totalReviews"])
        
        verify { vocabularyEntryRepository.findAll() }
        verify { vocabularyEntryRepository.findByNextReviewBefore(LocalDate.now()) }
        verify { vocabularyEntryRepository.findByNextReviewBefore(LocalDate.now().plusDays(2)) }
        verify { reviewHistoryRepository.findAll() }
        verify { reviewSessionRepository.findByStatus(ReviewSessionStatus.COMPLETED) }
    }

    @Test
    fun `getPeriodStatistics returns correct statistics for week period`() {
        // Given
        val today = LocalDate.now()
        val startOfWeek = today.minusDays(today.dayOfWeek.value - 1L)
        val entries = listOf(
            createVocabularyEntry(1, startOfWeek, 1),
            createVocabularyEntry(2, startOfWeek.plusDays(1), 2),
            createVocabularyEntry(3, today.plusDays(1), 3) // Not in period
        )
        val entriesInPeriod = entries.filter { it.nextReview >= startOfWeek && it.nextReview <= today }
        val reviewsInPeriod = listOf(
            ReviewHistory(
                id = 1L,
                vocabularyEntryId = 1L,
                reviewDate = startOfWeek.plusDays(1),
                qualityRating = 4,
                createdAt = LocalDateTime.now()
            )
        )

        every { vocabularyEntryRepository.findAll() } returns entries
        every { reviewHistoryRepository.findByReviewDateBetween(startOfWeek, today) } returns reviewsInPeriod

        // When
        val result = statisticsService.getPeriodStatistics("week")

        // Then
        assertEquals("week", result["period"])
        assertEquals(startOfWeek, result["startDate"])
        assertEquals(today, result["endDate"])
        assertEquals(entriesInPeriod.size, result["entriesDueInPeriod"])
        assertEquals(reviewsInPeriod.size, result["reviewsInPeriod"])
        assertEquals(reviewsInPeriod.map { it.qualityRating }.average(), result["averageQualityRating"])
        
        verify { vocabularyEntryRepository.findAll() }
        verify { reviewHistoryRepository.findByReviewDateBetween(startOfWeek, today) }
    }

    @Test
    fun `getPeriodStatistics throws exception for invalid period`() {
        // When/Then
        assertThrows<IllegalArgumentException> {
            statisticsService.getPeriodStatistics("invalid")
        }
    }

    @Test
    fun `getCategoryStatistics returns correct statistics`() {
        // Given
        val category = Category.NOUNS
        val entries = listOf(
            createVocabularyEntry(1, LocalDate.now(), 1, category),
            createVocabularyEntry(2, LocalDate.now(), 2, category),
            createVocabularyEntry(3, LocalDate.now(), 3, Category.VERBS) // Different category
        )
        val entriesInCategory = entries.filter { it.category == category }
        val reviewHistory = listOf(
            ReviewHistory(
                id = 1L,
                vocabularyEntryId = 1L,
                reviewDate = LocalDate.now(),
                qualityRating = 4,
                createdAt = LocalDateTime.now()
            )
        )

        every { vocabularyEntryRepository.findByCategory(category) } returns entriesInCategory
        every { reviewHistoryRepository.findByVocabularyEntryId(1L) } returns reviewHistory
        every { reviewHistoryRepository.findByVocabularyEntryId(2L) } returns emptyList()

        // When
        val result = statisticsService.getCategoryStatistics(category)

        // Then
        assertEquals(category, result["category"])
        assertEquals(entriesInCategory.size, result["totalEntries"])
        assertEquals(entriesInCategory.map { it.level }.average(), result["averageLevel"])
        assertEquals(entriesInCategory.count { it.nextReview <= LocalDate.now() }, result["entriesDueToday"])
        assertEquals(reviewHistory.size, result["totalReviews"])
        
        verify { vocabularyEntryRepository.findByCategory(category) }
        verify { reviewHistoryRepository.findByVocabularyEntryId(1L) }
        verify { reviewHistoryRepository.findByVocabularyEntryId(2L) }
    }

    @Test
    fun `getReviewHistoryStatistics returns correct statistics`() {
        // Given
        val entryId = 1L
        val reviewHistory = listOf(
            ReviewHistory(
                id = 1L,
                vocabularyEntryId = entryId,
                reviewDate = LocalDate.now(),
                qualityRating = 4,
                createdAt = LocalDateTime.now()
            ),
            ReviewHistory(
                id = 2L,
                vocabularyEntryId = entryId,
                reviewDate = LocalDate.now().minusDays(1),
                qualityRating = 5,
                createdAt = LocalDateTime.now().minusDays(1)
            )
        )

        every { reviewHistoryRepository.findByVocabularyEntryId(entryId) } returns reviewHistory

        // When
        val result = statisticsService.getReviewHistoryStatistics(entryId)

        // Then
        assertEquals(reviewHistory.size, result["totalReviews"])
        assertEquals(reviewHistory.map { it.qualityRating }.average(), result["averageQuality"])
        
        verify { reviewHistoryRepository.findByVocabularyEntryId(entryId) }
    }

    private fun createVocabularyEntry(
        id: Long, 
        nextReview: LocalDate, 
        level: Int = 1, 
        category: Category = Category.NOUNS
    ): VocabularyEntry {
        return VocabularyEntry(
            id = id,
            wordPt = "palavra$id",
            wordDe = "wort$id",
            example = "Example $id",
            level = level,
            nextReview = nextReview,
            category = category,
            createdAt = LocalDateTime.now()
        )
    }
}