package me.maximilianmilz.vocabulary.infrastructure.persistence.repository

import me.maximilianmilz.vocabulary.infrastructure.persistence.entity.VocabularyEntryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * Spring Data JPA repository for VocabularyEntryEntity.
 */
@Repository
interface JpaVocabularyEntryRepository : JpaRepository<VocabularyEntryEntity, Long> {
    /**
     * Find vocabulary entries by category.
     */
    fun findByCategory(category: String): List<VocabularyEntryEntity>
    
    /**
     * Find vocabulary entries due for review.
     */
    fun findByNextReviewBefore(date: LocalDate): List<VocabularyEntryEntity>
}