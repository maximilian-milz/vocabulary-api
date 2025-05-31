package me.maximilianmilz.vocabulary.domain.repository

import me.maximilianmilz.vocabulary.domain.model.ReviewSession
import me.maximilianmilz.vocabulary.domain.model.ReviewSessionStatus
import java.time.LocalDateTime

/**
 * Repository interface for review session operations.
 */
interface ReviewSessionRepository {
    /**
     * Find all review sessions.
     *
     * @return List of all review sessions
     */
    fun findAll(): List<ReviewSession>

    /**
     * Find a review session by ID.
     *
     * @param id The ID of the review session
     * @return The review session, or null if not found
     */
    fun findById(id: Long): ReviewSession?

    /**
     * Find review sessions by status.
     *
     * @param status The status of the review sessions
     * @return List of review sessions with the specified status
     */
    fun findByStatus(status: ReviewSessionStatus): List<ReviewSession>

    /**
     * Find review sessions by start time range.
     *
     * @param startDateTime The start of the time range
     * @param endDateTime The end of the time range
     * @return List of review sessions within the time range
     */
    fun findByStartTimeBetween(startDateTime: LocalDateTime, endDateTime: LocalDateTime): List<ReviewSession>

    /**
     * Find review sessions that include a specific vocabulary entry.
     *
     * @param vocabularyEntryId The ID of the vocabulary entry
     * @return List of review sessions that include the vocabulary entry
     */
    fun findByVocabularyEntryId(vocabularyEntryId: Long): List<ReviewSession>

    /**
     * Save a review session.
     *
     * @param reviewSession The review session to save
     * @return The saved review session
     */
    fun save(reviewSession: ReviewSession): ReviewSession

    /**
     * Delete a review session.
     *
     * @param id The ID of the review session to delete
     */
    fun deleteById(id: Long)

    /**
     * Add a vocabulary entry to a review session.
     *
     * @param sessionId The ID of the review session
     * @param vocabularyEntryId The ID of the vocabulary entry
     * @return The updated review session
     */
    fun addVocabularyEntry(sessionId: Long, vocabularyEntryId: Long): ReviewSession

    /**
     * Update the review result for a vocabulary entry in a session.
     *
     * @param sessionId The ID of the review session
     * @param vocabularyEntryId The ID of the vocabulary entry
     * @param qualityRating The quality rating of the review
     * @return The updated review session
     */
    fun updateReviewResult(sessionId: Long, vocabularyEntryId: Long, qualityRating: Int): ReviewSession

    /**
     * Complete a review session.
     *
     * @param sessionId The ID of the review session
     * @return The completed review session
     */
    fun completeSession(sessionId: Long): ReviewSession

    /**
     * Abandon a review session.
     *
     * @param sessionId The ID of the review session
     * @return The abandoned review session
     */
    fun abandonSession(sessionId: Long): ReviewSession
}