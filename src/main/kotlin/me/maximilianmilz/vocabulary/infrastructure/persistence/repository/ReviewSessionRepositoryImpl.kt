package me.maximilianmilz.vocabulary.infrastructure.persistence.repository

import me.maximilianmilz.vocabulary.domain.model.ReviewSession
import me.maximilianmilz.vocabulary.domain.model.ReviewSessionEntry
import me.maximilianmilz.vocabulary.domain.model.ReviewSessionStatus
import me.maximilianmilz.vocabulary.domain.repository.ReviewSessionRepository
import me.maximilianmilz.vocabulary.domain.repository.VocabularyEntryRepository
import me.maximilianmilz.vocabulary.infrastructure.persistence.entity.ReviewSessionEntity
import me.maximilianmilz.vocabulary.infrastructure.persistence.entity.ReviewSessionEntryEntity
import me.maximilianmilz.vocabulary.infrastructure.persistence.entity.ReviewSessionEntryId
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * Implementation of the ReviewSessionRepository interface.
 */
@Component
class ReviewSessionRepositoryImpl(
    private val jpaSessionRepository: JpaReviewSessionRepository,
    private val jpaSessionEntryRepository: JpaReviewSessionEntryRepository,
    private val vocabularyEntryRepository: VocabularyEntryRepository
) : ReviewSessionRepository {

    /**
     * Find all review sessions.
     *
     * @return List of all review sessions
     */
    override fun findAll(): List<ReviewSession> {
        return jpaSessionRepository.findAll().map { it.toDomain() }
    }

    /**
     * Find a review session by ID.
     *
     * @param id The ID of the review session
     * @return The review session, or null if not found
     */
    override fun findById(id: Long): ReviewSession? {
        val sessionEntity = jpaSessionRepository.findById(id).orElse(null) ?: return null
        val entries = jpaSessionEntryRepository.findByIdSessionId(id)
        return sessionEntity.toDomain(entries)
    }

    /**
     * Find review sessions by status.
     *
     * @param status The status of the review sessions
     * @return List of review sessions with the specified status
     */
    override fun findByStatus(status: ReviewSessionStatus): List<ReviewSession> {
        return jpaSessionRepository.findByStatus(status).map { 
            val entries = jpaSessionEntryRepository.findByIdSessionId(it.id!!)
            it.toDomain(entries)
        }
    }

    /**
     * Find review sessions by start time range.
     *
     * @param startDateTime The start of the time range
     * @param endDateTime The end of the time range
     * @return List of review sessions within the time range
     */
    override fun findByStartTimeBetween(startDateTime: LocalDateTime, endDateTime: LocalDateTime): List<ReviewSession> {
        return jpaSessionRepository.findByStartTimeBetween(startDateTime, endDateTime).map {
            val entries = jpaSessionEntryRepository.findByIdSessionId(it.id!!)
            it.toDomain(entries)
        }
    }

    /**
     * Find review sessions that include a specific vocabulary entry.
     *
     * @param vocabularyEntryId The ID of the vocabulary entry
     * @return List of review sessions that include the vocabulary entry
     */
    override fun findByVocabularyEntryId(vocabularyEntryId: Long): List<ReviewSession> {
        return jpaSessionRepository.findByVocabularyEntryId(vocabularyEntryId).map {
            val entries = jpaSessionEntryRepository.findByIdSessionId(it.id!!)
            it.toDomain(entries)
        }
    }

    /**
     * Save a review session.
     *
     * @param reviewSession The review session to save
     * @return The saved review session
     */
    @Transactional
    override fun save(reviewSession: ReviewSession): ReviewSession {
        // Save the session entity
        val sessionEntity = reviewSession.toEntity()
        val savedSessionEntity = jpaSessionRepository.save(sessionEntity)

        // Save the session entries
        val savedEntries = reviewSession.entries.map { entry ->
            val entryEntity = entry.toEntity(savedSessionEntity.id!!)
            jpaSessionEntryRepository.save(entryEntity)
        }

        return savedSessionEntity.toDomain(savedEntries)
    }

    /**
     * Delete a review session.
     *
     * @param id The ID of the review session to delete
     */
    @Transactional
    override fun deleteById(id: Long) {
        jpaSessionRepository.deleteById(id)
    }

    /**
     * Add a vocabulary entry to a review session.
     *
     * @param sessionId The ID of the review session
     * @param vocabularyEntryId The ID of the vocabulary entry
     * @return The updated review session
     */
    @Transactional
    override fun addVocabularyEntry(sessionId: Long, vocabularyEntryId: Long): ReviewSession {
        // Check if the session exists
        val sessionEntity = jpaSessionRepository.findById(sessionId).orElseThrow {
            IllegalArgumentException("Review session not found with id: $sessionId")
        }

        // Check if the entry already exists in the session
        val entryId = ReviewSessionEntryId(sessionId, vocabularyEntryId)
        if (jpaSessionEntryRepository.existsById(entryId)) {
            throw IllegalArgumentException("Vocabulary entry already exists in the session")
        }

        // Create and save the new entry
        val entryEntity = ReviewSessionEntryEntity(
            id = entryId,
            reviewed = false,
            qualityRating = null,
            reviewTime = null
        )
        jpaSessionEntryRepository.save(entryEntity)

        // Update the session's total entries count
        val updatedSessionEntity = sessionEntity.copy(
            totalEntries = sessionEntity.totalEntries + 1
        )
        val savedSessionEntity = jpaSessionRepository.save(updatedSessionEntity)

        // Get all entries for the session
        val entries = jpaSessionEntryRepository.findByIdSessionId(sessionId)

        return savedSessionEntity.toDomain(entries)
    }

    /**
     * Update the review result for a vocabulary entry in a session.
     *
     * @param sessionId The ID of the review session
     * @param vocabularyEntryId The ID of the vocabulary entry
     * @param qualityRating The quality rating of the review
     * @return The updated review session
     */
    @Transactional
    override fun updateReviewResult(sessionId: Long, vocabularyEntryId: Long, qualityRating: Int): ReviewSession {
        // Check if the session exists
        val sessionEntity = jpaSessionRepository.findById(sessionId).orElseThrow {
            IllegalArgumentException("Review session not found with id: $sessionId")
        }

        // Check if the entry exists in the session
        val entryId = ReviewSessionEntryId(sessionId, vocabularyEntryId)
        val entryEntity = jpaSessionEntryRepository.findById(entryId).orElseThrow {
            IllegalArgumentException("Vocabulary entry not found in the session")
        }

        // Update the entry
        val updatedEntryEntity = entryEntity.copy(
            reviewed = true,
            qualityRating = qualityRating,
            reviewTime = LocalDateTime.now()
        )
        jpaSessionEntryRepository.save(updatedEntryEntity)

        // Update the session's completed entries count
        val completedEntries = jpaSessionEntryRepository.findByIdSessionIdAndReviewed(sessionId, true).size
        val updatedSessionEntity = sessionEntity.copy(
            completedEntries = completedEntries
        )
        val savedSessionEntity = jpaSessionRepository.save(updatedSessionEntity)

        // Get all entries for the session
        val entries = jpaSessionEntryRepository.findByIdSessionId(sessionId)

        return savedSessionEntity.toDomain(entries)
    }

    /**
     * Complete a review session.
     *
     * @param sessionId The ID of the review session
     * @return The completed review session
     */
    @Transactional
    override fun completeSession(sessionId: Long): ReviewSession {
        // Check if the session exists
        val sessionEntity = jpaSessionRepository.findById(sessionId).orElseThrow {
            IllegalArgumentException("Review session not found with id: $sessionId")
        }

        // Update the session status
        val updatedSessionEntity = sessionEntity.copy(
            status = ReviewSessionStatus.COMPLETED,
            endTime = LocalDateTime.now()
        )
        val savedSessionEntity = jpaSessionRepository.save(updatedSessionEntity)

        // Get all entries for the session
        val entries = jpaSessionEntryRepository.findByIdSessionId(sessionId)

        return savedSessionEntity.toDomain(entries)
    }

    /**
     * Abandon a review session.
     *
     * @param sessionId The ID of the review session
     * @return The abandoned review session
     */
    @Transactional
    override fun abandonSession(sessionId: Long): ReviewSession {
        // Check if the session exists
        val sessionEntity = jpaSessionRepository.findById(sessionId).orElseThrow {
            IllegalArgumentException("Review session not found with id: $sessionId")
        }

        // Update the session status
        val updatedSessionEntity = sessionEntity.copy(
            status = ReviewSessionStatus.ABANDONED,
            endTime = LocalDateTime.now()
        )
        val savedSessionEntity = jpaSessionRepository.save(updatedSessionEntity)

        // Get all entries for the session
        val entries = jpaSessionEntryRepository.findByIdSessionId(sessionId)

        return savedSessionEntity.toDomain(entries)
    }

    /**
     * Convert a ReviewSessionEntity to a ReviewSession domain model.
     *
     * @param entries The list of ReviewSessionEntryEntity objects
     * @return The ReviewSession domain model
     */
    private fun ReviewSessionEntity.toDomain(entries: List<ReviewSessionEntryEntity> = emptyList()): ReviewSession {
        val sessionEntries = entries.map { it.toDomain() }
        return ReviewSession(
            id = id,
            startTime = startTime,
            endTime = endTime,
            totalEntries = totalEntries,
            completedEntries = completedEntries,
            status = status,
            entries = sessionEntries,
            createdAt = createdAt
        )
    }

    /**
     * Convert a ReviewSessionEntryEntity to a ReviewSessionEntry domain model.
     *
     * @return The ReviewSessionEntry domain model
     */
    private fun ReviewSessionEntryEntity.toDomain(): ReviewSessionEntry {
        val vocabularyEntry = id.vocabularyEntryId.let { vocabularyEntryRepository.findById(it) }
        return ReviewSessionEntry(
            sessionId = id.sessionId,
            vocabularyEntryId = id.vocabularyEntryId,
            reviewed = reviewed,
            qualityRating = qualityRating,
            reviewTime = reviewTime,
            vocabularyEntry = vocabularyEntry
        )
    }

    /**
     * Convert a ReviewSession domain model to a ReviewSessionEntity.
     *
     * @return The ReviewSessionEntity
     */
    private fun ReviewSession.toEntity(): ReviewSessionEntity {
        return ReviewSessionEntity(
            id = id,
            startTime = startTime,
            endTime = endTime,
            totalEntries = totalEntries,
            completedEntries = completedEntries,
            status = status,
            createdAt = createdAt
        )
    }

    /**
     * Convert a ReviewSessionEntry domain model to a ReviewSessionEntryEntity.
     *
     * @param sessionId The ID of the session
     * @return The ReviewSessionEntryEntity
     */
    private fun ReviewSessionEntry.toEntity(sessionId: Long): ReviewSessionEntryEntity {
        // Get the session entity
        val sessionEntity = jpaSessionRepository.findById(sessionId).orElse(null)

        return ReviewSessionEntryEntity(
            id = ReviewSessionEntryId(sessionId, vocabularyEntryId),
            reviewed = reviewed,
            qualityRating = qualityRating,
            reviewTime = reviewTime,
            session = sessionEntity
        )
    }
}
