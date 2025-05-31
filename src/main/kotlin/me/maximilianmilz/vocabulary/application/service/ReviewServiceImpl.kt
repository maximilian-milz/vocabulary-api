package me.maximilianmilz.vocabulary.application.service

import me.maximilianmilz.vocabulary.domain.model.Category
import me.maximilianmilz.vocabulary.domain.model.ReviewHistory
import me.maximilianmilz.vocabulary.domain.model.ReviewSession
import me.maximilianmilz.vocabulary.domain.model.ReviewSessionEntry
import me.maximilianmilz.vocabulary.domain.model.ReviewSessionStatus
import me.maximilianmilz.vocabulary.domain.model.VocabularyEntry
import me.maximilianmilz.vocabulary.domain.repository.ReviewHistoryRepository
import me.maximilianmilz.vocabulary.domain.repository.ReviewSessionRepository
import me.maximilianmilz.vocabulary.domain.repository.VocabularyEntryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Implementation of the ReviewService interface.
 */
@Service
class ReviewServiceImpl(
    private val vocabularyEntryRepository: VocabularyEntryRepository,
    private val reviewHistoryRepository: ReviewHistoryRepository,
    private val reviewSessionRepository: ReviewSessionRepository,
    private val spacedRepetitionService: SpacedRepetitionService
) : ReviewService {

    /**
     * Get vocabulary entries due for review.
     *
     * @param limit Optional parameter to limit the number of entries returned
     * @return List of vocabulary entries due for review
     */
    override fun getEntriesDueForReview(limit: Int?): List<VocabularyEntry> {
        val entries = vocabularyEntryRepository.findByNextReviewBefore(LocalDate.now())
        return limit?.let { entries.take(it) } ?: entries
    }

    /**
     * Record a review result for a vocabulary entry.
     *
     * @param entryId The ID of the vocabulary entry
     * @param qualityRating The quality rating of the review (0-5)
     * @param notes Optional notes about the review
     * @return The updated vocabulary entry
     */
    @Transactional
    override fun recordReviewResult(entryId: Long, qualityRating: Int, notes: String?): VocabularyEntry {
        // Find the vocabulary entry
        val entry = vocabularyEntryRepository.findById(entryId)
            ?: throw IllegalArgumentException("Vocabulary entry not found with id: $entryId")

        // Process the review result using the spaced repetition service
        val updatedEntry = spacedRepetitionService.processReviewResult(entry, qualityRating)

        // Save the updated entry
        val savedEntry = vocabularyEntryRepository.save(updatedEntry)

        // Create and save a review history entry
        val reviewHistory = ReviewHistory(
            vocabularyEntryId = entryId,
            reviewDate = LocalDate.now(),
            qualityRating = qualityRating,
            notes = notes,
            createdAt = LocalDateTime.now()
        )
        reviewHistoryRepository.save(reviewHistory)

        return savedEntry
    }

    /**
     * Get review history for a vocabulary entry.
     *
     * @param entryId The ID of the vocabulary entry
     * @return List of review history entries for the vocabulary entry
     */
    override fun getReviewHistory(entryId: Long): List<ReviewHistory> {
        // Check if the vocabulary entry exists
        vocabularyEntryRepository.findById(entryId)
            ?: throw IllegalArgumentException("Vocabulary entry not found with id: $entryId")

        return reviewHistoryRepository.findByVocabularyEntryId(entryId)
    }

    /**
     * Get review history for a date range.
     *
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return List of review history entries within the date range
     */
    override fun getReviewHistoryByDateRange(startDate: LocalDate, endDate: LocalDate): List<ReviewHistory> {
        if (endDate.isBefore(startDate)) {
            throw IllegalArgumentException("End date cannot be before start date")
        }
        return reviewHistoryRepository.findByReviewDateBetween(startDate, endDate)
    }

    /**
     * Start a new review session.
     *
     * @param limit Optional parameter to limit the number of entries in the session
     * @param categoryName Optional parameter to filter entries by category
     * @return The created review session
     */
    @Transactional
    override fun startReviewSession(limit: Int?, categoryName: String?): ReviewSession {
        // Get entries due for review
        val entries = vocabularyEntryRepository.findByNextReviewBefore(LocalDate.now())

        // Filter by category if specified
        val filteredEntries = if (categoryName != null) {
            try {
                val category = Category.valueOf(categoryName)
                entries.filter { it.category == category }
            } catch (e: IllegalArgumentException) {
                entries
            }
        } else {
            entries
        }

        // Apply limit if specified
        val limitedEntries = limit?.let { filteredEntries.take(it) } ?: filteredEntries

        // Create a new session
        val session = ReviewSession(
            startTime = LocalDateTime.now(),
            totalEntries = limitedEntries.size,
            completedEntries = 0,
            status = ReviewSessionStatus.IN_PROGRESS,
            entries = limitedEntries.map { entry ->
                ReviewSessionEntry(
                    sessionId = 0, // Will be updated after session is saved
                    vocabularyEntryId = entry.id!!,
                    reviewed = false,
                    vocabularyEntry = entry
                )
            },
            createdAt = LocalDateTime.now()
        )

        // Save the session
        return reviewSessionRepository.save(session)
    }

    /**
     * Get a review session by ID.
     *
     * @param sessionId The ID of the review session
     * @return The review session
     */
    override fun getReviewSession(sessionId: Long): ReviewSession {
        return reviewSessionRepository.findById(sessionId)
            ?: throw IllegalArgumentException("Review session not found with id: $sessionId")
    }

    /**
     * Update a review result in a session.
     *
     * @param sessionId The ID of the review session
     * @param entryId The ID of the vocabulary entry
     * @param qualityRating The quality rating of the review (0-5)
     * @return The updated review session
     */
    @Transactional
    override fun updateReviewResult(sessionId: Long, entryId: Long, qualityRating: Int): ReviewSession {
        // Update the review result in the session
        val updatedSession = reviewSessionRepository.updateReviewResult(sessionId, entryId, qualityRating)

        // Record the review result for the vocabulary entry
        recordReviewResult(entryId, qualityRating)

        return updatedSession
    }

    /**
     * Complete a review session.
     *
     * @param sessionId The ID of the review session
     * @return The completed review session
     */
    @Transactional
    override fun completeReviewSession(sessionId: Long): ReviewSession {
        return reviewSessionRepository.completeSession(sessionId)
    }

    /**
     * Abandon a review session.
     *
     * @param sessionId The ID of the review session
     * @return The abandoned review session
     */
    @Transactional
    override fun abandonReviewSession(sessionId: Long): ReviewSession {
        return reviewSessionRepository.abandonSession(sessionId)
    }

    /**
     * Get review sessions by status.
     *
     * @param status The status of the review sessions
     * @return List of review sessions with the specified status
     */
    override fun getReviewSessionsByStatus(status: String): List<ReviewSession> {
        try {
            val sessionStatus = ReviewSessionStatus.valueOf(status)
            return reviewSessionRepository.findByStatus(sessionStatus)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid status: $status. Valid values are ${ReviewSessionStatus.values().joinToString()}")
        }
    }

    /**
     * Get review sessions by date range.
     *
     * @param startDateTime The start of the time range
     * @param endDateTime The end of the time range
     * @return List of review sessions within the time range
     */
    override fun getReviewSessionsByDateRange(startDateTime: LocalDateTime, endDateTime: LocalDateTime): List<ReviewSession> {
        if (endDateTime.isBefore(startDateTime)) {
            throw IllegalArgumentException("End date cannot be before start date")
        }
        return reviewSessionRepository.findByStartTimeBetween(startDateTime, endDateTime)
    }
}