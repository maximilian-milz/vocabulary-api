package me.maximilianmilz.vocabulary.api.controller

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
class VocabularyEntrySearchController(
    private val repository: VocabularyEntryRepository,
    private val mapper: VocabularyEntryMapper
) {

    @GetMapping("/word-pt")
    fun searchByWordPt(
        @RequestParam query: String
    ): ResponseEntity<List<VocabularyEntryResponseDto>> {
        val entries = repository.findByWordPt(query)
        return ResponseEntity.ok(mapper.toResponseDtoList(entries))
    }

    @GetMapping("/word-de")
    fun searchByWordDe(
        @RequestParam query: String
    ): ResponseEntity<List<VocabularyEntryResponseDto>> {
        val entries = repository.findByWordDe(query)
        return ResponseEntity.ok(mapper.toResponseDtoList(entries))
    }

    @GetMapping("/example")
    fun searchByExample(
        @RequestParam query: String
    ): ResponseEntity<List<VocabularyEntryResponseDto>> {
        val entries = repository.findByExample(query)
        return ResponseEntity.ok(mapper.toResponseDtoList(entries))
    }

    @GetMapping("/word")
    fun searchByWord(
        @RequestParam query: String
    ): ResponseEntity<List<VocabularyEntryResponseDto>> {
        val entries = repository.findByWord(query)
        return ResponseEntity.ok(mapper.toResponseDtoList(entries))
    }

    @GetMapping("/category/{category}")
    fun getEntriesByCategory(
        @PathVariable category: Category
    ): ResponseEntity<List<VocabularyEntryResponseDto>> {
        val entries = repository.findByCategory(category)
        return ResponseEntity.ok(mapper.toResponseDtoList(entries))
    }

    @GetMapping("/due")
    fun getEntriesDueForReview(): ResponseEntity<List<VocabularyEntryResponseDto>> {
        val entries = repository.findByNextReviewBefore(LocalDate.now())
        return ResponseEntity.ok(mapper.toResponseDtoList(entries))
    }
}