package me.maximilianmilz.vocabulary.api.dto

import me.maximilianmilz.vocabulary.domain.model.Category
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * DTO for returning vocabulary entry data to clients.
 */
data class VocabularyEntryResponseDto(
    val id: Long,
    val wordPt: String,
    val wordDe: String,
    val example: String,
    val level: Int,
    val nextReview: LocalDate,
    val category: Category,
    val createdAt: LocalDateTime,

    // Additional metadata fields
    val notes: String? = null,
    val pronunciation: String? = null,
    val tags: List<String>? = null,

    // Fields for spaced repetition algorithm
    val repetitions: Int? = null,
    val easeFactor: Double? = null,
    val lastReviewDate: LocalDate? = null
)
