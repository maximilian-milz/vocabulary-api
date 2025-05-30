package me.maximilianmilz.vocabulary.api.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.Valid
import jakarta.validation.constraints.NotEmpty

/**
 * DTO for bulk create operation.
 */
@Schema(description = "Request for creating multiple vocabulary entries at once")
data class BulkCreateRequestDto(
    @field:NotEmpty(message = "Entries list cannot be empty")
    @field:Valid
    @field:Schema(description = "List of vocabulary entries to create", required = true)
    val entries: List<VocabularyEntryRequestDto>
)

/**
 * DTO for bulk update operation.
 */
@Schema(description = "Request for updating multiple vocabulary entries at once")
data class BulkUpdateRequestDto(
    @field:NotEmpty(message = "Entries list cannot be empty")
    @field:Valid
    @field:Schema(description = "List of vocabulary entries to update", required = true)
    val entries: List<BulkUpdateEntryDto>
)

/**
 * DTO for a single entry in a bulk update operation.
 */
@Schema(description = "Entry to update in a bulk operation")
data class BulkUpdateEntryDto(
    @field:Schema(description = "ID of the vocabulary entry to update", required = true)
    val id: Long,

    @field:Valid
    @field:Schema(description = "Updated vocabulary entry data", required = true)
    val entry: VocabularyEntryRequestDto
)

/**
 * DTO for bulk delete operation.
 */
@Schema(description = "Request for deleting multiple vocabulary entries at once")
data class BulkDeleteRequestDto(
    @field:NotEmpty(message = "IDs list cannot be empty")
    @field:Schema(description = "List of vocabulary entry IDs to delete", required = true)
    val ids: List<Long>
)

/**
 * DTO for bulk operation response.
 */
@Schema(description = "Response for a bulk operation with success and failure information")
data class BulkOperationResponseDto(
    @field:Schema(description = "Number of successfully processed entries", required = true)
    val successCount: Int,

    @field:Schema(description = "Number of failed entries", required = true)
    val failureCount: Int,

    @field:Schema(description = "List of failures with details", required = true)
    val failures: List<FailureDto>
)

/**
 * DTO for a failure in a bulk operation.
 */
@Schema(description = "Details about a failed operation in a bulk request")
data class FailureDto(
    @field:Schema(description = "ID of the entry that failed (may be null for creation failures)", required = false)
    val id: Long?,

    @field:Schema(description = "Error message explaining the failure reason", required = true)
    val message: String
)
