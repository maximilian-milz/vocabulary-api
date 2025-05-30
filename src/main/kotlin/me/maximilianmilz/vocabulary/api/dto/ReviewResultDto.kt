package me.maximilianmilz.vocabulary.api.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull

/**
 * DTO for submitting a review result for a vocabulary entry.
 */
@Schema(description = "Review result for a vocabulary entry using the SuperMemo2 algorithm")
data class ReviewResultDto(
    @field:NotNull(message = "Quality rating is required")
    @field:Min(value = 0, message = "Quality rating must be between 0 and 5")
    @field:Max(value = 5, message = "Quality rating must be between 0 and 5")
    @field:Schema(description = "Quality rating of the review (0-5, where 0=complete blackout, 5=perfect recall)", required = true, minimum = "0", maximum = "5")
    val qualityRating: Int
)
