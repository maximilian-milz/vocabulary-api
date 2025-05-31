package me.maximilianmilz.vocabulary.api.controller

import jakarta.validation.Valid
import me.maximilianmilz.vocabulary.api.dto.ReviewResultDto
import me.maximilianmilz.vocabulary.api.dto.VocabularyEntryResponseDto
import me.maximilianmilz.vocabulary.api.exception.ResourceNotFoundException
import me.maximilianmilz.vocabulary.api.mapper.VocabularyEntryMapper
import me.maximilianmilz.vocabulary.application.service.ReviewService
import me.maximilianmilz.vocabulary.application.service.StatisticsService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

/**
 * REST controller for vocabulary review operations.
 */
@RestController
@RequestMapping("/api/reviews")
class ReviewController(
    private val reviewService: ReviewService,
    private val statisticsService: StatisticsService,
    private val mapper: VocabularyEntryMapper
) {

    /**
     * Get vocabulary entries due for review.
     * 
     * @param limit Optional parameter to limit the number of entries returned
     * @return List of vocabulary entries due for review
     */
    @GetMapping("/due")
    fun getEntriesDueForReview(
        @RequestParam(required = false) limit: Int?
    ): ResponseEntity<List<VocabularyEntryResponseDto>> {
        val entries = reviewService.getEntriesDueForReview(limit)
        return ResponseEntity.ok(mapper.toResponseDtoList(entries))
    }

    /**
     * Record a review result for a vocabulary entry.
     * 
     * @param id The ID of the vocabulary entry
     * @param reviewResultDto The review result data
     * @return The updated vocabulary entry
     */
    @PostMapping("/{id}")
    fun recordReviewResult(
        @PathVariable id: Long,
        @Valid @RequestBody reviewResultDto: ReviewResultDto
    ): ResponseEntity<VocabularyEntryResponseDto> {
        try {
            // Record the review result using the review service
            val updatedEntry = reviewService.recordReviewResult(id, reviewResultDto.quality, reviewResultDto.notes)

            // Return the updated entry
            return ResponseEntity.ok(mapper.toResponseDto(updatedEntry))
        } catch (e: IllegalArgumentException) {
            throw ResourceNotFoundException(e.message ?: "Vocabulary entry not found with id: $id")
        }
    }

    /**
     * Get review statistics for a vocabulary entry.
     * 
     * @param id The ID of the vocabulary entry
     * @return Review statistics for the vocabulary entry
     */
    @GetMapping("/statistics/{id}")
    fun getReviewStatistics(
        @PathVariable id: Long
    ): ResponseEntity<Map<String, Any?>> {
        try {
            // Get the review history statistics for the entry
            val statistics = statisticsService.getReviewHistoryStatistics(id)
            return ResponseEntity.ok(statistics)
        } catch (e: IllegalArgumentException) {
            throw ResourceNotFoundException(e.message ?: "Vocabulary entry not found with id: $id")
        }
    }

    /**
     * Get review history for a vocabulary entry.
     * 
     * @param id The ID of the vocabulary entry
     * @return Review history for the vocabulary entry
     */
    @GetMapping("/history/{id}")
    fun getReviewHistory(
        @PathVariable id: Long
    ): ResponseEntity<List<Map<String, Any?>>> {
        try {
            // Get the review history for the entry
            val reviewHistory = reviewService.getReviewHistory(id)

            // Convert to a list of maps for the response
            val history = reviewHistory.map { history ->
                mapOf(
                    "id" to history.id,
                    "date" to history.reviewDate,
                    "quality" to history.qualityRating,
                    "notes" to history.notes,
                    "createdAt" to history.createdAt
                )
            }

            return ResponseEntity.ok(history)
        } catch (e: IllegalArgumentException) {
            throw ResourceNotFoundException(e.message ?: "Vocabulary entry not found with id: $id")
        }
    }

    /**
     * Start a new review session.
     * 
     * @param sessionParams Optional parameters for the session
     * @return The created review session
     */
    @PostMapping("/sessions")
    fun startReviewSession(
        @RequestBody(required = false) sessionParams: Map<String, Any>?
    ): ResponseEntity<Map<String, Any?>> {
        // Extract parameters
        val limit = sessionParams?.get("limit") as? Int
        val categoryName = sessionParams?.get("category") as? String

        // Start a new review session
        val session = reviewService.startReviewSession(limit, categoryName)

        // Convert to a map for the response
        val sessionMap = mapOf(
            "sessionId" to session.id,
            "startTime" to session.startTime,
            "totalEntries" to session.totalEntries,
            "completedEntries" to session.completedEntries,
            "status" to session.status,
            "entries" to mapper.toResponseDtoList(session.entries.mapNotNull { it.vocabularyEntry })
        )

        return ResponseEntity.ok(sessionMap)
    }

    /**
     * Complete a review session.
     * 
     * @param sessionId The ID of the session to complete
     * @return The completed session
     */
    @PutMapping("/sessions/{sessionId}/complete")
    fun completeReviewSession(
        @PathVariable sessionId: Long
    ): ResponseEntity<Map<String, Any?>> {
        try {
            // Complete the review session
            val completedSession = reviewService.completeReviewSession(sessionId)

            // Convert to a map for the response
            val sessionMap = mapOf(
                "sessionId" to completedSession.id,
                "startTime" to completedSession.startTime,
                "endTime" to completedSession.endTime,
                "totalEntries" to completedSession.totalEntries,
                "completedEntries" to completedSession.completedEntries,
                "status" to completedSession.status
            )

            return ResponseEntity.ok(sessionMap)
        } catch (e: IllegalArgumentException) {
            throw ResourceNotFoundException(e.message ?: "Review session not found with id: $sessionId")
        }
    }
    /**
     * Get a review session by ID.
     * 
     * @param sessionId The ID of the session
     * @return The review session
     */
    @GetMapping("/sessions/{sessionId}")
    fun getReviewSession(
        @PathVariable sessionId: Long
    ): ResponseEntity<Map<String, Any?>> {
        try {
            // Get the review session
            val session = reviewService.getReviewSession(sessionId)

            // Convert to a map for the response
            val sessionMap = mapOf(
                "sessionId" to session.id,
                "startTime" to session.startTime,
                "endTime" to session.endTime,
                "totalEntries" to session.totalEntries,
                "completedEntries" to session.completedEntries,
                "status" to session.status,
                "entries" to session.entries.map { entry ->
                    mapOf(
                        "vocabularyEntryId" to entry.vocabularyEntryId,
                        "reviewed" to entry.reviewed,
                        "qualityRating" to entry.qualityRating,
                        "reviewTime" to entry.reviewTime,
                        "vocabularyEntry" to entry.vocabularyEntry?.let { mapper.toResponseDto(it) }
                    )
                }
            )

            return ResponseEntity.ok(sessionMap)
        } catch (e: IllegalArgumentException) {
            throw ResourceNotFoundException(e.message ?: "Review session not found with id: $sessionId")
        }
    }

    /**
     * Update a review result in a session.
     * 
     * @param sessionId The ID of the session
     * @param entryId The ID of the vocabulary entry
     * @param reviewResultDto The review result data
     * @return The updated session
     */
    @PostMapping("/sessions/{sessionId}/entries/{entryId}")
    fun updateReviewResult(
        @PathVariable sessionId: Long,
        @PathVariable entryId: Long,
        @Valid @RequestBody reviewResultDto: ReviewResultDto
    ): ResponseEntity<Map<String, Any?>> {
        try {
            // Update the review result
            val updatedSession = reviewService.updateReviewResult(sessionId, entryId, reviewResultDto.quality)

            // Convert to a map for the response
            val sessionMap = mapOf(
                "sessionId" to updatedSession.id,
                "startTime" to updatedSession.startTime,
                "endTime" to updatedSession.endTime,
                "totalEntries" to updatedSession.totalEntries,
                "completedEntries" to updatedSession.completedEntries,
                "status" to updatedSession.status
            )

            return ResponseEntity.ok(sessionMap)
        } catch (e: IllegalArgumentException) {
            throw ResourceNotFoundException(e.message ?: "Review session or vocabulary entry not found")
        }
    }

    /**
     * Abandon a review session.
     * 
     * @param sessionId The ID of the session to abandon
     * @return The abandoned session
     */
    @PutMapping("/sessions/{sessionId}/abandon")
    fun abandonReviewSession(
        @PathVariable sessionId: Long
    ): ResponseEntity<Map<String, Any?>> {
        try {
            // Abandon the review session
            val abandonedSession = reviewService.abandonReviewSession(sessionId)

            // Convert to a map for the response
            val sessionMap = mapOf(
                "sessionId" to abandonedSession.id,
                "startTime" to abandonedSession.startTime,
                "endTime" to abandonedSession.endTime,
                "totalEntries" to abandonedSession.totalEntries,
                "completedEntries" to abandonedSession.completedEntries,
                "status" to abandonedSession.status
            )

            return ResponseEntity.ok(sessionMap)
        } catch (e: IllegalArgumentException) {
            throw ResourceNotFoundException(e.message ?: "Review session not found with id: $sessionId")
        }
    }
}
