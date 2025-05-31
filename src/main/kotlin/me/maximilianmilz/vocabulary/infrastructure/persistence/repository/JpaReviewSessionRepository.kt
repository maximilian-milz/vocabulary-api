package me.maximilianmilz.vocabulary.infrastructure.persistence.repository

import me.maximilianmilz.vocabulary.domain.model.ReviewSessionStatus
import me.maximilianmilz.vocabulary.infrastructure.persistence.entity.ReviewSessionEntity
import me.maximilianmilz.vocabulary.infrastructure.persistence.entity.ReviewSessionEntryEntity
import me.maximilianmilz.vocabulary.infrastructure.persistence.entity.ReviewSessionEntryId
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

/**
 * JPA repository interface for review session operations.
 */
@Repository
interface JpaReviewSessionRepository : JpaRepository<ReviewSessionEntity, Long> {
    /**
     * Find review sessions by status.
     *
     * @param status The status of the review sessions
     * @return List of review sessions with the specified status
     */
    fun findByStatus(status: ReviewSessionStatus): List<ReviewSessionEntity>

    /**
     * Find review sessions by start time range.
     *
     * @param startDateTime The start of the time range
     * @param endDateTime The end of the time range
     * @return List of review sessions within the time range
     */
    fun findByStartTimeBetween(startDateTime: LocalDateTime, endDateTime: LocalDateTime): List<ReviewSessionEntity>

    /**
     * Find review sessions that include a specific vocabulary entry.
     *
     * @param vocabularyEntryId The ID of the vocabulary entry
     * @return List of review sessions that include the vocabulary entry
     */
    @Query("SELECT s FROM ReviewSessionEntity s JOIN s.entries e WHERE e.id.vocabularyEntryId = :vocabularyEntryId")
    fun findByVocabularyEntryId(vocabularyEntryId: Long): List<ReviewSessionEntity>
}

/**
 * JPA repository interface for review session entry operations.
 */
@Repository
interface JpaReviewSessionEntryRepository : JpaRepository<ReviewSessionEntryEntity, ReviewSessionEntryId> {
    /**
     * Find review session entries by session ID.
     *
     * @param sessionId The ID of the session
     * @return List of review session entries for the session
     */
    fun findByIdSessionId(sessionId: Long): List<ReviewSessionEntryEntity>

    /**
     * Find review session entries by vocabulary entry ID.
     *
     * @param vocabularyEntryId The ID of the vocabulary entry
     * @return List of review session entries for the vocabulary entry
     */
    fun findByIdVocabularyEntryId(vocabularyEntryId: Long): List<ReviewSessionEntryEntity>

    /**
     * Find review session entries by session ID and reviewed status.
     *
     * @param sessionId The ID of the session
     * @param reviewed The reviewed status
     * @return List of review session entries for the session with the specified reviewed status
     */
    fun findByIdSessionIdAndReviewed(sessionId: Long, reviewed: Boolean): List<ReviewSessionEntryEntity>
}