package me.maximilianmilz.vocabulary.infrastructure.persistence.repository

import me.maximilianmilz.vocabulary.infrastructure.persistence.entity.ReviewHistoryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * JPA repository interface for review history operations.
 */
@Repository
interface JpaReviewHistoryRepository : JpaRepository<ReviewHistoryEntity, Long> {
    /**
     * Find review history entries by vocabulary entry ID.
     *
     * @param vocabularyEntryId The ID of the vocabulary entry
     * @return List of review history entries for the vocabulary entry
     */
    fun findByVocabularyEntryId(vocabularyEntryId: Long): List<ReviewHistoryEntity>

    /**
     * Find review history entries by review date.
     *
     * @param reviewDate The review date
     * @return List of review history entries for the review date
     */
    fun findByReviewDate(reviewDate: LocalDate): List<ReviewHistoryEntity>

    /**
     * Find review history entries by review date range.
     *
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return List of review history entries within the date range
     */
    fun findByReviewDateBetween(startDate: LocalDate, endDate: LocalDate): List<ReviewHistoryEntity>
}