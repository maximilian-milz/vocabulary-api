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
    val category: String,
    val createdAt: LocalDateTime
)