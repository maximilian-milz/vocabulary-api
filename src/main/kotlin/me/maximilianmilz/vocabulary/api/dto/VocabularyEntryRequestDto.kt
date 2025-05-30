package me.maximilianmilz.vocabulary.api.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import me.maximilianmilz.vocabulary.domain.model.Category
import java.time.LocalDate

/**
 * DTO for creating or updating a vocabulary entry.
 */
data class VocabularyEntryRequestDto(
    @field:NotBlank(message = "Portuguese word is required")
    val wordPt: String,

    @field:NotBlank(message = "German word is required")
    val wordDe: String,

    @field:NotBlank(message = "Example is required")
    val example: String,

    @field:NotNull(message = "Level is required")
    @field:Min(value = 1, message = "Level must be at least 1")
    val level: Int,

    @field:NotNull(message = "Next review date is required")
    val nextReview: LocalDate,

    @field:NotNull(message = "Category is required")
    val category: Category,

    // Additional metadata fields (optional)
    val notes: String? = null,

    val pronunciation: String? = null,

    val tags: List<String>? = null
)
