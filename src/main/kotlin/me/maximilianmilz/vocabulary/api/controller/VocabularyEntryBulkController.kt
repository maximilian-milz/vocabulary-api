package me.maximilianmilz.vocabulary.api.controller

import jakarta.validation.Valid
import me.maximilianmilz.vocabulary.api.dto.*
import me.maximilianmilz.vocabulary.application.service.BulkOperationService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for bulk operations on vocabulary entries.
 */
@RestController
@RequestMapping("/api/vocabulary-entries/bulk")
class VocabularyEntryBulkController(
    private val bulkOperationService: BulkOperationService
) {

    @PostMapping("/create")
    fun bulkCreate(
        @Valid @RequestBody request: BulkCreateRequestDto
    ): ResponseEntity<BulkOperationResponseDto> {
        val result = bulkOperationService.bulkCreate(request)
        return ResponseEntity.ok(result)
    }

    @PutMapping("/update")
    fun bulkUpdate(
        @Valid @RequestBody request: BulkUpdateRequestDto
    ): ResponseEntity<BulkOperationResponseDto> {
        val result = bulkOperationService.bulkUpdate(request)
        return ResponseEntity.ok(result)
    }

    @DeleteMapping("/delete")
    fun bulkDelete(
        @Valid @RequestBody request: BulkDeleteRequestDto
    ): ResponseEntity<BulkOperationResponseDto> {
        val result = bulkOperationService.bulkDelete(request)
        return ResponseEntity.ok(result)
    }

    @GetMapping("/export", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun exportEntries(): ResponseEntity<List<VocabularyEntryResponseDto>> {
        val entries = bulkOperationService.exportEntries()
        return ResponseEntity.ok(entries)
    }

    @PostMapping("/import")
    fun importEntries(
        @Valid @RequestBody entries: List<VocabularyEntryRequestDto>
    ): ResponseEntity<BulkOperationResponseDto> {
        val result = bulkOperationService.importEntries(entries)
        return ResponseEntity.ok(result)
    }
}