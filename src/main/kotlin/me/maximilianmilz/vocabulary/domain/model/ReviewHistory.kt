package me.maximilianmilz.vocabulary.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Domain entity representing a review history entry.
 */
data class ReviewHistory(
    val id: Long? = null,
    val vocabularyEntryId: Long,
    val reviewDate: LocalDate,
    val qualityRating: Int,
    val notes: String? = null,
    val createdAt: LocalDateTime
)