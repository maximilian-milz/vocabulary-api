package me.maximilianmilz.vocabulary.api.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.ArraySchema
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import me.maximilianmilz.vocabulary.api.dto.VocabularyEntryResponseDto
import me.maximilianmilz.vocabulary.api.mapper.VocabularyEntryMapper
import me.maximilianmilz.vocabulary.domain.model.Category
import me.maximilianmilz.vocabulary.domain.repository.VocabularyEntryRepository
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

/**
 * REST controller for searching vocabulary entries.
 */
@RestController
@RequestMapping("/api/vocabulary-entries/search")
@Tag(name = "Vocabulary Entries Search", description = "API for searching vocabulary entries")
class VocabularyEntrySearchController(
    private val repository: VocabularyEntryRepository,
    private val mapper: VocabularyEntryMapper
) {

    @GetMapping("/word-pt")
    @Operation(
        summary = "Search vocabulary entries by Portuguese word",
        description = "Returns vocabulary entries that match the Portuguese word search query"
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Successfully retrieved matching entries",
            content = [Content(array = ArraySchema(schema = Schema(implementation = VocabularyEntryResponseDto::class)))]
        )
    )
    fun searchByWordPt(
        @Parameter(description = "Portuguese word search query", required = true)
        @RequestParam query: String
    ): ResponseEntity<List<VocabularyEntryResponseDto>> {
        val entries = repository.findByWordPt(query)
        return ResponseEntity.ok(mapper.toResponseDtoList(entries))
    }

    @GetMapping("/word-de")
    @Operation(
        summary = "Search vocabulary entries by German word",
        description = "Returns vocabulary entries that match the German word search query"
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Successfully retrieved matching entries",
            content = [Content(array = ArraySchema(schema = Schema(implementation = VocabularyEntryResponseDto::class)))]
        )
    )
    fun searchByWordDe(
        @Parameter(description = "German word search query", required = true)
        @RequestParam query: String
    ): ResponseEntity<List<VocabularyEntryResponseDto>> {
        val entries = repository.findByWordDe(query)
        return ResponseEntity.ok(mapper.toResponseDtoList(entries))
    }

    @GetMapping("/example")
    @Operation(
        summary = "Search vocabulary entries by example text",
        description = "Returns vocabulary entries that match the example text search query"
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Successfully retrieved matching entries",
            content = [Content(array = ArraySchema(schema = Schema(implementation = VocabularyEntryResponseDto::class)))]
        )
    )
    fun searchByExample(
        @Parameter(description = "Example text search query", required = true)
        @RequestParam query: String
    ): ResponseEntity<List<VocabularyEntryResponseDto>> {
        val entries = repository.findByExample(query)
        return ResponseEntity.ok(mapper.toResponseDtoList(entries))
    }

    @GetMapping("/word")
    @Operation(
        summary = "Search vocabulary entries by word in either Portuguese or German",
        description = "Returns vocabulary entries that match the word search query in either Portuguese or German"
    )
    @ApiResponses(
        ApiResponse(
            responseCode = "200", description = "Successfully retrieved matching entries",
            content = [Content(array = ArraySchema(schema = Schema(implementation = VocabularyEntryResponseDto::class)))]
        )
    )
    fun searchByWord(
        @Parameter(description = "Word search query", required = true)
        @RequestParam query: String
    ): ResponseEntity<List<VocabularyEntryResponseDto>> {
        val entries = repository.findByWord(query)
        return ResponseEntity.ok(mapper.toResponseDtoList(entries))
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
        @PathVariable category: Category
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
}