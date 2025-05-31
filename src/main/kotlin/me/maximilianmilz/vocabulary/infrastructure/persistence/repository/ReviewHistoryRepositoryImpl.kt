package me.maximilianmilz.vocabulary.infrastructure.persistence.repository

import me.maximilianmilz.vocabulary.domain.model.ReviewHistory
import me.maximilianmilz.vocabulary.domain.repository.ReviewHistoryRepository
import me.maximilianmilz.vocabulary.infrastructure.persistence.entity.ReviewHistoryEntity
import org.springframework.stereotype.Component
import java.time.LocalDate

/**
 * Implementation of the ReviewHistoryRepository interface.
 */
@Component
class ReviewHistoryRepositoryImpl(
    private val jpaRepository: JpaReviewHistoryRepository
) : ReviewHistoryRepository {

    /**
     * Find all review history entries.
     *
     * @return List of all review history entries
     */
    override fun findAll(): List<ReviewHistory> {
        return jpaRepository.findAll().map { it.toDomain() }
    }

    /**
     * Find a review history entry by ID.
     *
     * @param id The ID of the review history entry
     * @return The review history entry, or null if not found
     */
    override fun findById(id: Long): ReviewHistory? {
        return jpaRepository.findById(id).orElse(null)?.toDomain()
    }

    /**
     * Find review history entries by vocabulary entry ID.
     *
     * @param vocabularyEntryId The ID of the vocabulary entry
     * @return List of review history entries for the vocabulary entry
     */
    override fun findByVocabularyEntryId(vocabularyEntryId: Long): List<ReviewHistory> {
        return jpaRepository.findByVocabularyEntryId(vocabularyEntryId).map { it.toDomain() }
    }

    /**
     * Find review history entries by review date.
     *
     * @param reviewDate The review date
     * @return List of review history entries for the review date
     */
    override fun findByReviewDate(reviewDate: LocalDate): List<ReviewHistory> {
        return jpaRepository.findByReviewDate(reviewDate).map { it.toDomain() }
    }

    /**
     * Find review history entries by review date range.
     *
     * @param startDate The start date of the range
     * @param endDate The end date of the range
     * @return List of review history entries within the date range
     */
    override fun findByReviewDateBetween(startDate: LocalDate, endDate: LocalDate): List<ReviewHistory> {
        return jpaRepository.findByReviewDateBetween(startDate, endDate).map { it.toDomain() }
    }

    /**
     * Save a review history entry.
     *
     * @param reviewHistory The review history entry to save
     * @return The saved review history entry
     */
    override fun save(reviewHistory: ReviewHistory): ReviewHistory {
        val entity = reviewHistory.toEntity()
        return jpaRepository.save(entity).toDomain()
    }

    /**
     * Delete a review history entry.
     *
     * @param id The ID of the review history entry to delete
     */
    override fun deleteById(id: Long) {
        jpaRepository.deleteById(id)
    }

    /**
     * Convert a ReviewHistoryEntity to a ReviewHistory domain model.
     *
     * @return The ReviewHistory domain model
     */
    private fun ReviewHistoryEntity.toDomain(): ReviewHistory {
        return ReviewHistory(
            id = id,
            vocabularyEntryId = vocabularyEntryId,
            reviewDate = reviewDate,
            qualityRating = qualityRating,
            notes = notes,
            createdAt = createdAt
        )
    }

    /**
     * Convert a ReviewHistory domain model to a ReviewHistoryEntity.
     *
     * @return The ReviewHistoryEntity
     */
    private fun ReviewHistory.toEntity(): ReviewHistoryEntity {
        return ReviewHistoryEntity(
            id = id,
            vocabularyEntryId = vocabularyEntryId,
            reviewDate = reviewDate,
            qualityRating = qualityRating,
            notes = notes,
            createdAt = createdAt
        )
    }
}