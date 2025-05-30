package me.maximilianmilz.vocabulary.api.mapper

import me.maximilianmilz.vocabulary.api.dto.VocabularyEntryRequestDto
import me.maximilianmilz.vocabulary.api.dto.VocabularyEntryResponseDto
import me.maximilianmilz.vocabulary.domain.model.Category
import me.maximilianmilz.vocabulary.domain.model.VocabularyEntry
import org.springframework.stereotype.Component
import java.time.LocalDateTime

/**
 * Mapper for converting between VocabularyEntry domain model and DTOs.
 */
@Component
class VocabularyEntryMapper {

    /**
     * Convert a domain model to a response DTO.
     */
    fun toResponseDto(vocabularyEntry: VocabularyEntry): VocabularyEntryResponseDto {
        return VocabularyEntryResponseDto(
            id = vocabularyEntry.id!!,
            wordPt = vocabularyEntry.wordPt,
            wordDe = vocabularyEntry.wordDe,
            example = vocabularyEntry.example,
            level = vocabularyEntry.level,
            nextReview = vocabularyEntry.nextReview,
            category = vocabularyEntry.category,
            createdAt = vocabularyEntry.createdAt,
            notes = vocabularyEntry.notes,
            pronunciation = vocabularyEntry.pronunciation,
            tags = vocabularyEntry.tags,
            repetitions = vocabularyEntry.repetitions,
            easeFactor = vocabularyEntry.easeFactor,
            lastReviewDate = vocabularyEntry.lastReviewDate
        )
    }

    /**
     * Convert a list of domain models to a list of response DTOs.
     */
    fun toResponseDtoList(vocabularyEntries: List<VocabularyEntry>): List<VocabularyEntryResponseDto> {
        return vocabularyEntries.map { toResponseDto(it) }
    }

    /**
     * Convert a request DTO to a domain model for creating a new entry.
     */
    fun toDomainModel(requestDto: VocabularyEntryRequestDto): VocabularyEntry {
        return VocabularyEntry(
            id = null,
            wordPt = requestDto.wordPt,
            wordDe = requestDto.wordDe,
            example = requestDto.example,
            level = requestDto.level,
            nextReview = requestDto.nextReview,
            category = requestDto.category,
            createdAt = LocalDateTime.now(),
            notes = requestDto.notes,
            pronunciation = requestDto.pronunciation,
            tags = requestDto.tags
        )
    }

    /**
     * Convert a request DTO to a domain model for updating an existing entry.
     */
    fun toDomainModel(id: Long, requestDto: VocabularyEntryRequestDto, createdAt: LocalDateTime): VocabularyEntry {
        return VocabularyEntry(
            id = id,
            wordPt = requestDto.wordPt,
            wordDe = requestDto.wordDe,
            example = requestDto.example,
            level = requestDto.level,
            nextReview = requestDto.nextReview,
            category = requestDto.category,
            createdAt = createdAt,
            notes = requestDto.notes,
            pronunciation = requestDto.pronunciation,
            tags = requestDto.tags
        )
    }
}
