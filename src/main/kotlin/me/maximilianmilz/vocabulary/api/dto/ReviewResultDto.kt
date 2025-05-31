package me.maximilianmilz.vocabulary.api.dto

import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

/**
 * DTO for submitting a review result for a vocabulary entry.
 */
data class ReviewResultDto(
    @field:NotNull(message = "Quality rating is required")
    @field:Min(value = 0, message = "Quality rating must be between 0 and 5")
    @field:Max(value = 5, message = "Quality rating must be between 0 and 5")
    val quality: Int,

    val notes: String? = null
)
