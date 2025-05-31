package me.maximilianmilz.vocabulary.domain.repository

import me.maximilianmilz.vocabulary.domain.model.ReviewHistory
import java.time.LocalDate

/**
 * Repository interface for review history operations.
 */
interface ReviewHistoryRepository {
    /**
     * Find all review history entries.
     *
     * @return List of all review history entries
     */
    fun findAll(): List<ReviewHistory>

    /**
     * Find a review history entry by ID.
     *
     * @param id The ID of the review history entry
     * @return The review history entry, or null if not found
     */
    fun findById(id: Long): ReviewHistory?

    /**
     * Find review history entries by vocabulary entry ID.
     *
     * @param vocabularyEntryId The ID of the vocabulary entry
     * @return List of review history entries for the vocabulary entry
     */
    fun findByVocabularyEntryId(vocabularyEntryId: Long): List<ReviewHistory>

    /**
     * Find review history entries by review date.
     *
     * @param reviewDate The review date
     * @return List of review history entries for the review date
     */
    fun findByReviewDate(reviewDate: LocalDate): List<ReviewHistory>

    /**
     * Find review history entries by review date range.
     *
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return List of review history entries within the date range
     */
    fun findByReviewDateBetween(startDate: LocalDate, endDate: LocalDate): List<ReviewHistory>

    /**
     * Save a review history entry.
     *
     * @param reviewHistory The review history entry to save
     * @return The saved review history entry
     */
    fun save(reviewHistory: ReviewHistory): ReviewHistory

    /**
     * Delete a review history entry.
     *
     * @param id The ID of the review history entry to delete
     */
    fun deleteById(id: Long)
}