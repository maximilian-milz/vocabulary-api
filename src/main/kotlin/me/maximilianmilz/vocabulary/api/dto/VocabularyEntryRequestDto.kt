package me.maximilianmilz.vocabulary.api.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import me.maximilianmilz.vocabulary.domain.model.Category
import java.time.LocalDate

/**
 * DTO for creating or updating a vocabulary entry.
 */
@Schema(description = "Data for creating or updating a vocabulary entry")
data class VocabularyEntryRequestDto(
    @field:NotBlank(message = "Portuguese word is required")
    @field:Schema(description = "The word in Portuguese", required = true, example = "casa")
    val wordPt: String,

    @field:NotBlank(message = "German word is required")
    @field:Schema(description = "The word in German", required = true, example = "Haus")
    val wordDe: String,

    @field:NotBlank(message = "Example is required")
    @field:Schema(description = "An example sentence using the word", required = true, example = "Eu moro em uma casa grande.")
    val example: String,

    @field:NotNull(message = "Level is required")
    @field:Min(value = 1, message = "Level must be at least 1")
    @field:Schema(description = "Difficulty level of the vocabulary entry", required = true, minimum = "1", example = "2")
    val level: Int,

    @field:NotNull(message = "Next review date is required")
    @field:Schema(description = "Date when the entry should be reviewed next", required = true, example = "2023-12-31")
    val nextReview: LocalDate,

    @field:NotNull(message = "Category is required")
    @field:Schema(description = "Category of the vocabulary entry", required = true, enumAsRef = true)
    val category: Category,

    // Additional metadata fields (optional)
    @field:Schema(description = "Additional notes about the vocabulary entry", required = false)
    val notes: String? = null,

    @field:Schema(description = "Pronunciation guide for the word", required = false)
    val pronunciation: String? = null,

    @field:Schema(description = "Tags associated with the vocabulary entry", required = false)
    val tags: List<String>? = null
)
