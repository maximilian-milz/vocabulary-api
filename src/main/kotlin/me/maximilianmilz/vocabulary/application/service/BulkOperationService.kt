package me.maximilianmilz.vocabulary.application.service

import me.maximilianmilz.vocabulary.api.dto.*
import me.maximilianmilz.vocabulary.api.mapper.VocabularyEntryMapper
import me.maximilianmilz.vocabulary.domain.model.VocabularyEntry
import me.maximilianmilz.vocabulary.domain.repository.VocabularyEntryRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * Service for handling bulk operations on vocabulary entries.
 */
@Service
class BulkOperationService(
    private val repository: VocabularyEntryRepository,
    private val mapper: VocabularyEntryMapper
) {

    /**
     * Bulk create vocabulary entries.
     */
    @Transactional
    fun bulkCreate(request: BulkCreateRequestDto): BulkOperationResponseDto {
        val successes = mutableListOf<VocabularyEntry>()
        val failures = mutableListOf<FailureDto>()

        request.entries.forEach { requestDto ->
            try {
                val entry = mapper.toDomainModel(requestDto)
                val savedEntry = repository.save(entry)
                successes.add(savedEntry)
            } catch (e: Exception) {
                failures.add(FailureDto(null, "Failed to create entry: ${e.message}"))
            }
        }

        return BulkOperationResponseDto(
            successCount = successes.size,
            failureCount = failures.size,
            failures = failures
        )
    }

    /**
     * Bulk update vocabulary entries.
     */
    @Transactional
    fun bulkUpdate(request: BulkUpdateRequestDto): BulkOperationResponseDto {
        val successes = mutableListOf<VocabularyEntry>()
        val failures = mutableListOf<FailureDto>()

        request.entries.forEach { updateEntry ->
            try {
                val existingEntry = repository.findById(updateEntry.id)
                if (existingEntry != null) {
                    val updatedEntry = mapper.toDomainModel(updateEntry.id, updateEntry.entry, existingEntry.createdAt)
                    val savedEntry = repository.save(updatedEntry)
                    successes.add(savedEntry)
                } else {
                    failures.add(FailureDto(updateEntry.id, "Entry not found"))
                }
            } catch (e: Exception) {
                failures.add(FailureDto(updateEntry.id, "Failed to update entry: ${e.message}"))
            }
        }

        return BulkOperationResponseDto(
            successCount = successes.size,
            failureCount = failures.size,
            failures = failures
        )
    }

    /**
     * Bulk delete vocabulary entries.
     */
    @Transactional
    fun bulkDelete(request: BulkDeleteRequestDto): BulkOperationResponseDto {
        val successes = mutableListOf<Long>()
        val failures = mutableListOf<FailureDto>()

        request.ids.forEach { id ->
            try {
                if (repository.findById(id) != null) {
                    repository.deleteById(id)
                    successes.add(id)
                } else {
                    failures.add(FailureDto(id, "Entry not found"))
                }
            } catch (e: Exception) {
                failures.add(FailureDto(id, "Failed to delete entry: ${e.message}"))
            }
        }

        return BulkOperationResponseDto(
            successCount = successes.size,
            failureCount = failures.size,
            failures = failures
        )
    }

    /**
     * Export vocabulary entries to a list of DTOs.
     */
    fun exportEntries(): List<VocabularyEntryResponseDto> {
        val entries = repository.findAll()
        return entries.map { mapper.toResponseDto(it) }
    }

    /**
     * Import vocabulary entries from a list of DTOs.
     */
    @Transactional
    fun importEntries(entries: List<VocabularyEntryRequestDto>): BulkOperationResponseDto {
        return bulkCreate(BulkCreateRequestDto(entries))
    }
}