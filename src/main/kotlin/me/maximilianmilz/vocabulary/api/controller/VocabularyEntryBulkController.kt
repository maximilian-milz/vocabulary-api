package me.maximilianmilz.vocabulary.api.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import me.maximilianmilz.vocabulary.api.dto.*
import me.maximilianmilz.vocabulary.api.exception.ErrorResponse
import me.maximilianmilz.vocabulary.application.service.BulkOperationService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for bulk operations on vocabulary entries.
 */
@RestController
@RequestMapping("/api/vocabulary-entries/bulk")
@Tag(name = "Vocabulary Entries Bulk Operations", description = "API for bulk operations on vocabulary entries")
class VocabularyEntryBulkController(
    private val bulkOperationService: BulkOperationService
) {

    @PostMapping("/create")
    @Operation(
        summary = "Bulk create vocabulary entries",
        description = "Creates multiple vocabulary entries in a single request"
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Successfully processed the bulk create operation",
            content = [Content(schema = Schema(implementation = BulkOperationResponseDto::class))]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    fun bulkCreate(
        @Parameter(description = "Bulk create request data", required = true)
        @Valid @RequestBody request: BulkCreateRequestDto
    ): ResponseEntity<BulkOperationResponseDto> {
        val result = bulkOperationService.bulkCreate(request)
        return ResponseEntity.ok(result)
    }

    @PutMapping("/update")
    @Operation(
        summary = "Bulk update vocabulary entries",
        description = "Updates multiple vocabulary entries in a single request"
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Successfully processed the bulk update operation",
            content = [Content(schema = Schema(implementation = BulkOperationResponseDto::class))]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    fun bulkUpdate(
        @Parameter(description = "Bulk update request data", required = true)
        @Valid @RequestBody request: BulkUpdateRequestDto
    ): ResponseEntity<BulkOperationResponseDto> {
        val result = bulkOperationService.bulkUpdate(request)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/delete")
    @Operation(
        summary = "Bulk delete vocabulary entries",
        description = "Deletes multiple vocabulary entries in a single request"
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Successfully processed the bulk delete operation",
            content = [Content(schema = Schema(implementation = BulkOperationResponseDto::class))]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    fun bulkDelete(
        @Parameter(description = "Bulk delete request data", required = true)
        @Valid @RequestBody request: BulkDeleteRequestDto
    ): ResponseEntity<BulkOperationResponseDto> {
        val result = bulkOperationService.bulkDelete(request)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/export", produces = [MediaType.APPLICATION_JSON_VALUE])
    @Operation(
        summary = "Export vocabulary entries",
        description = "Exports all vocabulary entries as JSON"
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Successfully exported vocabulary entries",
            content = [Content(array = ArraySchema(schema = Schema(implementation = VocabularyEntryResponseDto::class)))]
        )
    )
    fun exportEntries(): ResponseEntity<List<VocabularyEntryResponseDto>> {
        val entries = bulkOperationService.exportEntries()
        return ResponseEntity.ok(entries)
    }

    @PostMapping("/import")
    @Operation(
        summary = "Import vocabulary entries",
        description = "Imports vocabulary entries from JSON"
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Successfully processed the import operation",
            content = [Content(schema = Schema(implementation = BulkOperationResponseDto::class))]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    fun importEntries(
        @Parameter(description = "Vocabulary entries to import", required = true)
        @Valid @RequestBody entries: List<VocabularyEntryRequestDto>
    ): ResponseEntity<BulkOperationResponseDto> {
        val result = bulkOperationService.importEntries(entries)
        return ResponseEntity.ok(result)
    }
}