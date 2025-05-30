package me.maximilianmilz.vocabulary.infrastructure.persistence.repository

import me.maximilianmilz.vocabulary.domain.model.Category
import me.maximilianmilz.vocabulary.domain.model.VocabularyEntry
import me.maximilianmilz.vocabulary.domain.repository.VocabularyEntryRepository
import me.maximilianmilz.vocabulary.infrastructure.persistence.entity.VocabularyEntryEntity
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * Implementation of the VocabularyEntryRepository interface using JPA.
 */
@Repository
class VocabularyEntryRepositoryImpl(
    private val jpaRepository: JpaVocabularyEntryRepository
) : VocabularyEntryRepository {

    override fun findById(id: Long): VocabularyEntry? {
        return jpaRepository.findById(id).map { it.toDomain() }.orElse(null)
    }

    override fun findAll(): List<VocabularyEntry> {
        return jpaRepository.findAll().map { it.toDomain() }
    }

    override fun findByCategory(category: Category): List<VocabularyEntry> {
        return jpaRepository.findByCategory(category.name).map { it.toDomain() }
    }

    override fun findByNextReviewBefore(date: LocalDate): List<VocabularyEntry> {
        return jpaRepository.findByNextReviewBefore(date).map { it.toDomain() }
    }

    override fun findByWordPt(wordPt: String): List<VocabularyEntry> {
        return jpaRepository.findByWordPtContainingIgnoreCase(wordPt).map { it.toDomain() }
    }

    override fun findByWordDe(wordDe: String): List<VocabularyEntry> {
        return jpaRepository.findByWordDeContainingIgnoreCase(wordDe).map { it.toDomain() }
    }

    override fun findByExample(example: String): List<VocabularyEntry> {
        return jpaRepository.findByExampleContainingIgnoreCase(example).map { it.toDomain() }
    }

    override fun findByWord(word: String): List<VocabularyEntry> {
        return jpaRepository.findByWordPtOrWordDe(word).map { it.toDomain() }
    }

    override fun save(vocabularyEntry: VocabularyEntry): VocabularyEntry {
        val entity = vocabularyEntry.toEntity()
        return jpaRepository.save(entity).toDomain()
    }

    override fun deleteById(id: Long) {
        jpaRepository.deleteById(id)
    }

    /**
     * Extension function to convert a VocabularyEntryEntity to a VocabularyEntry domain model.
     */
    private fun VocabularyEntryEntity.toDomain(): VocabularyEntry {
        return VocabularyEntry(
            id = this.id,
            wordPt = this.wordPt,
            wordDe = this.wordDe,
            example = this.example,
            level = this.level,
            nextReview = this.nextReview,
            category = this.category,
            createdAt = this.createdAt,
            notes = this.notes,
            pronunciation = this.pronunciation,
            tags = this.tags,
            repetitions = this.repetitions,
            easeFactor = this.easeFactor,
            lastReviewDate = this.lastReviewDate
        )
    }

    /**
     * Extension function to convert a VocabularyEntry domain model to a VocabularyEntryEntity.
     */
    private fun VocabularyEntry.toEntity(): VocabularyEntryEntity {
        return VocabularyEntryEntity(
            id = this.id,
            wordPt = this.wordPt,
            wordDe = this.wordDe,
            example = this.example,
            level = this.level,
            nextReview = this.nextReview,
            category = this.category,
            createdAt = this.createdAt,
            notes = this.notes,
            pronunciation = this.pronunciation,
            tags = this.tags,
            repetitions = this.repetitions,
            easeFactor = this.easeFactor,
            lastReviewDate = this.lastReviewDate
        )
    }
}
