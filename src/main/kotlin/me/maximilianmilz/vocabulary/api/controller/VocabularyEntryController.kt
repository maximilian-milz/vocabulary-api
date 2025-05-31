package me.maximilianmilz.vocabulary.api.controller

import jakarta.validation.Valid
import me.maximilianmilz.vocabulary.api.dto.*
import me.maximilianmilz.vocabulary.api.exception.ResourceNotFoundException
import me.maximilianmilz.vocabulary.api.mapper.VocabularyEntryMapper
import me.maximilianmilz.vocabulary.application.service.SpacedRepetitionService
import me.maximilianmilz.vocabulary.domain.repository.VocabularyEntryRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

/**
 * REST controller for basic CRUD operations on vocabulary entries.
 */
@RestController
@RequestMapping("/api/vocabulary-entries")
class VocabularyEntryController(
    private val repository: VocabularyEntryRepository,
    private val mapper: VocabularyEntryMapper,
    private val spacedRepetitionService: SpacedRepetitionService
) {

    @GetMapping
    fun getAllEntries(): ResponseEntity<List<VocabularyEntryResponseDto>> {
        val entries = repository.findAll()
        return ResponseEntity.ok(mapper.toResponseDtoList(entries))
    }

    @GetMapping("/{id}")
    fun getEntryById(
        @PathVariable id: Long
    ): ResponseEntity<VocabularyEntryResponseDto> {
        val entry = repository.findById(id)
            ?: throw ResourceNotFoundException("Vocabulary entry not found with id: $id")

        return ResponseEntity.ok(mapper.toResponseDto(entry))
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun createEntry(
        @Valid @RequestBody requestDto: VocabularyEntryRequestDto
    ): ResponseEntity<VocabularyEntryResponseDto> {
        val domainModel = mapper.toDomainModel(requestDto)
        val savedEntry = repository.save(domainModel)

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponseDto(savedEntry))
    }

    @PutMapping("/{id}")
    fun updateEntry(
        @PathVariable id: Long,
        @Valid @RequestBody requestDto: VocabularyEntryRequestDto
    ): ResponseEntity<VocabularyEntryResponseDto> {
        val existingEntry = repository.findById(id)
            ?: throw ResourceNotFoundException("Vocabulary entry not found with id: $id")

        val updatedEntry = mapper.toDomainModel(id, requestDto, existingEntry.createdAt)
        val savedEntry = repository.save(updatedEntry)

        return ResponseEntity.ok(mapper.toResponseDto(savedEntry))
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteEntry(
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        if (repository.findById(id) == null) {
            throw ResourceNotFoundException("Vocabulary entry not found with id: $id")
        }

        repository.deleteById(id)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/{id}/review")
    fun recordReviewResult(
        @PathVariable id: Long,
        @Valid @RequestBody reviewResultDto: ReviewResultDto
    ): ResponseEntity<VocabularyEntryResponseDto> {
        // Find the vocabulary entry
        val entry = repository.findById(id)
            ?: throw ResourceNotFoundException("Vocabulary entry not found with id: $id")

        // Process the review result using the spaced repetition service
        val updatedEntry = spacedRepetitionService.processReviewResult(entry, reviewResultDto.quality)

        // Save the updated entry
        val savedEntry = repository.save(updatedEntry)

        // Return the updated entry
        return ResponseEntity.ok(mapper.toResponseDto(savedEntry))
    }
}
