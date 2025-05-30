package me.maximilianmilz.vocabulary.api.dto

import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty

/**
 * DTO for bulk create operation.
 */
data class BulkCreateRequestDto(
    @field:NotEmpty(message = "Entries list cannot be empty")
    @field:Valid
    val entries: List<VocabularyEntryRequestDto>
)

/**
 * DTO for bulk update operation.
 */
data class BulkUpdateRequestDto(
    @field:NotEmpty(message = "Entries list cannot be empty")
    @field:Valid
    val entries: List<BulkUpdateEntryDto>
)

/**
 * DTO for a single entry in a bulk update operation.
 */
data class BulkUpdateEntryDto(
    val id: Long,

    @field:Valid
    val entry: VocabularyEntryRequestDto
)

/**
 * DTO for bulk delete operation.
 */
data class BulkDeleteRequestDto(
    @field:NotEmpty(message = "IDs list cannot be empty")
    val ids: List<Long>
)

/**
 * DTO for bulk operation response.
 */
data class BulkOperationResponseDto(
    val successCount: Int,
    val failureCount: Int,
    val failures: List<FailureDto>
)

/**
 * DTO for a failure in a bulk operation.
 */
data class FailureDto(
    val id: Long?,
    val message: String
)
