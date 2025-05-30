package me.maximilianmilz.vocabulary.domain.repository

import me.maximilianmilz.vocabulary.domain.model.Category
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
    fun findByCategory(category: Category): List<VocabularyEntry>

    /**
     * Find vocabulary entries due for review.
     */
    fun findByNextReviewBefore(date: LocalDate): List<VocabularyEntry>

    /**
     * Find vocabulary entries by Portuguese word (case-insensitive, partial match).
     */
    fun findByWordPt(wordPt: String): List<VocabularyEntry>

    /**
     * Find vocabulary entries by German word (case-insensitive, partial match).
     */
    fun findByWordDe(wordDe: String): List<VocabularyEntry>

    /**
     * Find vocabulary entries by example text (case-insensitive, partial match).
     */
    fun findByExample(example: String): List<VocabularyEntry>

    /**
     * Find vocabulary entries by word in either Portuguese or German (case-insensitive, partial match).
     */
    fun findByWord(word: String): List<VocabularyEntry>

    /**
     * Save a vocabulary entry.
     */
    fun save(vocabularyEntry: VocabularyEntry): VocabularyEntry

    /**
     * Delete a vocabulary entry by its ID.
     */
    fun deleteById(id: Long)
}
