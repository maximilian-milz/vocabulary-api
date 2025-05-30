package me.maximilianmilz.vocabulary.api.dto

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
    val category: String,
    val createdAt: LocalDateTime
)