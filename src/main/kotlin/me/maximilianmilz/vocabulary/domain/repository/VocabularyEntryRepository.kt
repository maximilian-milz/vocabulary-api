package me.maximilianmilz.vocabulary.domain.repository

import me.maximilianmilz.vocabulary.domain.model.VocabularyEntry
import java.time.LocalDate

/**
 * Repository interface for VocabularyEntry domain entity.
 */
interface VocabularyEntryRepository {
    /**
     * Find a vocabulary entry by its ID.
     */
    fun findById(id: Long): VocabularyEntry?
    
    /**
     * Find all vocabulary entries.
     */
    fun findAll(): List<VocabularyEntry>
    
    /**
     * Find vocabulary entries by category.
     */
    fun findByCategory(category: String): List<VocabularyEntry>
    
    /**
     * Find vocabulary entries due for review.
     */
    fun findByNextReviewBefore(date: LocalDate): List<VocabularyEntry>
    
    /**
     * Save a vocabulary entry.
     */
    fun save(vocabularyEntry: VocabularyEntry): VocabularyEntry
    
    /**
     * Delete a vocabulary entry by its ID.
     */
    fun deleteById(id: Long)
}