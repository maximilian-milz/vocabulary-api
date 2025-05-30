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
import me.maximilianmilz.vocabulary.api.dto.VocabularyEntryRequestDto
import me.maximilianmilz.vocabulary.api.dto.VocabularyEntryResponseDto
import me.maximilianmilz.vocabulary.api.exception.ErrorResponse
import me.maximilianmilz.vocabulary.api.exception.ResourceNotFoundException
import me.maximilianmilz.vocabulary.api.mapper.VocabularyEntryMapper
import me.maximilianmilz.vocabulary.domain.repository.VocabularyEntryRepository
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

/**
 * REST controller for vocabulary entries.
 */
@RestController
@RequestMapping("/api/vocabulary-entries")
@Tag(name = "Vocabulary Entries", description = "API for managing vocabulary entries")
class VocabularyEntryController(
    private val repository: VocabularyEntryRepository,
    private val mapper: VocabularyEntryMapper
) {

    @GetMapping
    @Operation(summary = "Get all vocabulary entries", description = "Returns a list of all vocabulary entries")
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Successfully retrieved all entries",
            content = [Content(array = ArraySchema(schema = Schema(implementation = VocabularyEntryResponseDto::class)))]
        )
    )
    fun getAllEntries(): ResponseEntity<List<VocabularyEntryResponseDto>> {
        val entries = repository.findAll()
        return ResponseEntity.ok(mapper.toResponseDtoList(entries))
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get a vocabulary entry by ID", description = "Returns a vocabulary entry by its ID")
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Successfully retrieved the entry",
            content = [Content(schema = Schema(implementation = VocabularyEntryResponseDto::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Entry not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    fun getEntryById(
        @Parameter(description = "ID of the vocabulary entry", required = true)
        @PathVariable id: Long
    ): ResponseEntity<VocabularyEntryResponseDto> {
        val entry = repository.findById(id)
            ?: throw ResourceNotFoundException("Vocabulary entry not found with id: $id")

        return ResponseEntity.ok(mapper.toResponseDto(entry))
    }

    @GetMapping("/category/{category}")
    @Operation(
        summary = "Get vocabulary entries by category",
        description = "Returns vocabulary entries filtered by category"
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Successfully retrieved entries by category",
            content = [Content(array = ArraySchema(schema = Schema(implementation = VocabularyEntryResponseDto::class)))]
        )
    )
    fun getEntriesByCategory(
        @Parameter(description = "Category to filter by", required = true)
        @PathVariable category: String
    ): ResponseEntity<List<VocabularyEntryResponseDto>> {
        val entries = repository.findByCategory(category)
        return ResponseEntity.ok(mapper.toResponseDtoList(entries))
    }

    @GetMapping("/due")
    @Operation(
        summary = "Get vocabulary entries due for review",
        description = "Returns vocabulary entries that are due for review"
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Successfully retrieved entries due for review",
            content = [Content(array = ArraySchema(schema = Schema(implementation = VocabularyEntryResponseDto::class)))]
        )
    )
    fun getEntriesDueForReview(): ResponseEntity<List<VocabularyEntryResponseDto>> {
        val entries = repository.findByNextReviewBefore(LocalDate.now())
        return ResponseEntity.ok(mapper.toResponseDtoList(entries))
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new vocabulary entry", description = "Creates a new vocabulary entry")
    @ApiResponses(
        ApiResponse(
            responseCode = "201", description = "Successfully created the entry",
            content = [Content(schema = Schema(implementation = VocabularyEntryResponseDto::class))]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    fun createEntry(
        @Parameter(description = "Vocabulary entry to create", required = true)
        @Valid @RequestBody requestDto: VocabularyEntryRequestDto
    ): ResponseEntity<VocabularyEntryResponseDto> {
        val domainModel = mapper.toDomainModel(requestDto)
        val savedEntry = repository.save(domainModel)

        return ResponseEntity.status(HttpStatus.CREATED).body(mapper.toResponseDto(savedEntry))
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a vocabulary entry", description = "Updates an existing vocabulary entry")
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Successfully updated the entry",
            content = [Content(schema = Schema(implementation = VocabularyEntryResponseDto::class))]
        ),
        ApiResponse(
            responseCode = "400",
            description = "Invalid input",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "Entry not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    fun updateEntry(
        @Parameter(description = "ID of the vocabulary entry to update", required = true)
        @PathVariable id: Long,

        @Parameter(description = "Updated vocabulary entry data", required = true)
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
    @Operation(summary = "Delete a vocabulary entry", description = "Deletes a vocabulary entry by its ID")
    @ApiResponses(
        ApiResponse(responseCode = "204", description = "Successfully deleted the entry"),
        ApiResponse(
            responseCode = "404",
            description = "Entry not found",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    )
    fun deleteEntry(
        @Parameter(description = "ID of the vocabulary entry to delete", required = true)
        @PathVariable id: Long
    ): ResponseEntity<Void> {
        if (repository.findById(id) == null) {
            throw ResourceNotFoundException("Vocabulary entry not found with id: $id")
        }

        repository.deleteById(id)
        return ResponseEntity.noContent().build()
    }
}