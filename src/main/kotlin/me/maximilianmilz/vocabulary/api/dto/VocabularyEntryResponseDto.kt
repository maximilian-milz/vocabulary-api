package me.maximilianmilz.vocabulary.api.dto

import io.swagger.v3.oas.annotations.media.Schema
import me.maximilianmilz.vocabulary.domain.model.Category
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * DTO for returning vocabulary entry data to clients.
 */
@Schema(description = "Vocabulary entry data returned to clients")
data class VocabularyEntryResponseDto(
    @field:Schema(description = "Unique identifier of the vocabulary entry", required = true)
    val id: Long,

    @field:Schema(description = "The word in Portuguese", required = true, example = "casa")
    val wordPt: String,

    @field:Schema(description = "The word in German", required = true, example = "Haus")
    val wordDe: String,

    @field:Schema(description = "An example sentence using the word", required = true, example = "Eu moro em uma casa grande.")
    val example: String,

    @field:Schema(description = "Difficulty level of the vocabulary entry", required = true, minimum = "1", example = "2")
    val level: Int,

    @field:Schema(description = "Date when the entry should be reviewed next", required = true, example = "2023-12-31")
    val nextReview: LocalDate,

    @field:Schema(description = "Category of the vocabulary entry", required = true, enumAsRef = true)
    val category: Category,

    @field:Schema(description = "Date and time when the entry was created", required = true)
    val createdAt: LocalDateTime,

    // Additional metadata fields
    @field:Schema(description = "Additional notes about the vocabulary entry", required = false)
    val notes: String? = null,

    @field:Schema(description = "Pronunciation guide for the word", required = false)
    val pronunciation: String? = null,

    @field:Schema(description = "Tags associated with the vocabulary entry", required = false)
    val tags: List<String>? = null,

    // Fields for spaced repetition algorithm
    @field:Schema(description = "Number of times the entry has been reviewed", required = false)
    val repetitions: Int? = null,

    @field:Schema(description = "Ease factor for the SuperMemo2 algorithm", required = false)
    val easeFactor: Double? = null,

    @field:Schema(description = "Date when the entry was last reviewed", required = false)
    val lastReviewDate: LocalDate? = null
)
