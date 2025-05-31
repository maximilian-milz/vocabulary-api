package me.maximilianmilz.vocabulary.application.service

import me.maximilianmilz.vocabulary.domain.model.ReviewHistory
import me.maximilianmilz.vocabulary.domain.model.ReviewSession
import me.maximilianmilz.vocabulary.domain.model.VocabularyEntry
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Service interface for vocabulary review operations.
 */
interface ReviewService {
    /**
     * Get vocabulary entries due for review.
     *
     * @param limit Optional parameter to limit the number of entries returned
     * @return List of vocabulary entries due for review
     */
    fun getEntriesDueForReview(limit: Int? = null): List<VocabularyEntry>

    /**
     * Record a review result for a vocabulary entry.
     *
     * @param entryId The ID of the vocabulary entry
     * @param qualityRating The quality rating of the review (0-5)
     * @param notes Optional notes about the review
     * @return The updated vocabulary entry
     */
    fun recordReviewResult(entryId: Long, qualityRating: Int, notes: String? = null): VocabularyEntry

    /**
     * Get review history for a vocabulary entry.
     *
     * @param entryId The ID of the vocabulary entry
     * @return List of review history entries for the vocabulary entry
     */
    fun getReviewHistory(entryId: Long): List<ReviewHistory>

    /**
     * Get review history for a date range.
     *
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return List of review history entries within the date range
     */
    fun getReviewHistoryByDateRange(startDate: LocalDate, endDate: LocalDate): List<ReviewHistory>

    /**
     * Start a new review session.
     *
     * @param limit Optional parameter to limit the number of entries in the session
     * @param categoryName Optional parameter to filter entries by category
     * @return The created review session
     */
    fun startReviewSession(limit: Int? = null, categoryName: String? = null): ReviewSession

    /**
     * Get a review session by ID.
     *
     * @param sessionId The ID of the review session
     * @return The review session
     */
    fun getReviewSession(sessionId: Long): ReviewSession

    /**
     * Update a review result in a session.
     *
     * @param sessionId The ID of the review session
     * @param entryId The ID of the vocabulary entry
     * @param qualityRating The quality rating of the review (0-5)
     * @return The updated review session
     */
    fun updateReviewResult(sessionId: Long, entryId: Long, qualityRating: Int): ReviewSession

    /**
     * Complete a review session.
     *
     * @param sessionId The ID of the review session
     * @return The completed review session
     */
    fun completeReviewSession(sessionId: Long): ReviewSession

    /**
     * Abandon a review session.
     *
     * @param sessionId The ID of the review session
     * @return The abandoned review session
     */
    fun abandonReviewSession(sessionId: Long): ReviewSession

    /**
     * Get review sessions by status.
     *
     * @param status The status of the review sessions
     * @return List of review sessions with the specified status
     */
    fun getReviewSessionsByStatus(status: String): List<ReviewSession>

    /**
     * Get review sessions by date range.
     *
     * @param startDateTime The start of the time range
     * @param endDateTime The end of the time range
     * @return List of review sessions within the time range
     */
    fun getReviewSessionsByDateRange(startDateTime: LocalDateTime, endDateTime: LocalDateTime): List<ReviewSession>
}