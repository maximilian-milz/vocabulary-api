package me.maximilianmilz.vocabulary.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * Domain entity representing a vocabulary entry.
 */
data class VocabularyEntry(
    val id: Long? = null,
    val wordPt: String,
    val wordDe: String,
    val example: String,
    val level: Int,
    val nextReview: LocalDate,
    val category: Category,
    val createdAt: LocalDateTime,

    // Additional metadata fields
    val notes: String? = null,            // Additional notes about the word
    val pronunciation: String? = null,    // Pronunciation guide
    val tags: List<String>? = null,       // Tags for additional categorization

    // Fields for spaced repetition algorithm
    val repetitions: Int? = null,         // Number of successful reviews
    val easeFactor: Double? = null,       // Ease factor for SM-2 algorithm
    val lastReviewDate: LocalDate? = null // Date of the last review
)
