package me.maximilianmilz.vocabulary.domain.model

import java.time.LocalDateTime

/**
 * Domain entity representing a review session.
 */
data class ReviewSession(
    val id: Long? = null,
    val startTime: LocalDateTime,
    val endTime: LocalDateTime? = null,
    val totalEntries: Int,
    val completedEntries: Int = 0,
    val status: ReviewSessionStatus = ReviewSessionStatus.IN_PROGRESS,
    val entries: List<ReviewSessionEntry> = emptyList(),
    val createdAt: LocalDateTime
)

/**
 * Enum representing the status of a review session.
 */
enum class ReviewSessionStatus {
    IN_PROGRESS,
    COMPLETED,
    ABANDONED
}

/**
 * Domain entity representing a vocabulary entry in a review session.
 */
data class ReviewSessionEntry(
    val sessionId: Long,
    val vocabularyEntryId: Long,
    val reviewed: Boolean = false,
    val qualityRating: Int? = null,
    val reviewTime: LocalDateTime? = null,
    val vocabularyEntry: VocabularyEntry? = null
)