package me.maximilianmilz.vocabulary.application.service

import me.maximilianmilz.vocabulary.domain.model.VocabularyEntry
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import kotlin.math.max
import kotlin.math.roundToInt

/**
 * Implementation of the SpacedRepetitionService using the SuperMemo-2 algorithm.
 * 
 * The SuperMemo-2 algorithm is a spaced repetition algorithm that calculates
 * optimal intervals between reviews based on the quality of recall.
 */
@Service
class SuperMemo2SpacedRepetitionService : SpacedRepetitionService {

    companion object {
        // Default ease factor for new items
        const val DEFAULT_EASE_FACTOR = 2.5

        // Minimum ease factor to prevent intervals from becoming too short
        const val MIN_EASE_FACTOR = 1.3
    }

    /**
     * Process a review result for a vocabulary entry and calculate the next review date.
     *
     * @param entry The vocabulary entry being reviewed
     * @param qualityRating The quality of the response (0-5)
     * @return The updated vocabulary entry with new review schedule
     */
    override fun processReviewResult(entry: VocabularyEntry, qualityRating: Int): VocabularyEntry {
        // Ensure quality rating is within valid range
        val validQualityRating = qualityRating.coerceIn(0, 5)

        // Get current values or defaults if not set
        val currentRepetitions = entry.repetitions ?: 0
        val currentEaseFactor = entry.easeFactor ?: DEFAULT_EASE_FACTOR

        // Calculate days since last review (or 0 if this is the first review)
        val previousInterval = if (entry.lastReviewDate != null) {
            ChronoUnit.DAYS.between(entry.lastReviewDate, LocalDate.now()).toInt()
        } else {
            0
        }

        // Calculate new values based on the quality rating
        val (newRepetitions, newEaseFactor, newInterval) = if (validQualityRating < 3) {
            // If quality is less than 3, reset repetitions and review again soon
            Triple(0, currentEaseFactor, 1)
        } else {
            // Calculate new ease factor
            val newEaseFactor = calculateNewEaseFactor(currentEaseFactor, validQualityRating)

            // Calculate new interval based on repetitions
            val newInterval = when (currentRepetitions) {
                0 -> 1 // First successful review: 1 day
                1 -> 6 // Second successful review: 6 days
                else -> (previousInterval * newEaseFactor).roundToInt() // Subsequent reviews
            }

            Triple(currentRepetitions + 1, newEaseFactor, newInterval)
        }

        // Calculate the next review date
        val nextReviewDate = LocalDate.now().plusDays(newInterval.toLong())

        // Return updated vocabulary entry
        return entry.copy(
            repetitions = newRepetitions,
            easeFactor = newEaseFactor,
            lastReviewDate = LocalDate.now(),
            nextReview = nextReviewDate
        )
    }

    /**
     * Calculate the next review date based on the spaced repetition algorithm.
     *
     * @param repetitions Number of consecutive correct reviews
     * @param easeFactor The ease factor for the entry
     * @param previousInterval The previous interval in days
     * @return The next review date
     */
    override fun calculateNextReviewDate(repetitions: Int, easeFactor: Double, previousInterval: Int): LocalDate {
        val interval = when (repetitions) {
            0 -> 1 // First review: 1 day
            1 -> 6 // Second review: 6 days
            else -> (previousInterval * easeFactor).roundToInt() // Subsequent reviews
        }

        return LocalDate.now().plusDays(interval.toLong())
    }

    /**
     * Calculate the new ease factor based on the quality rating.
     *
     * @param currentEaseFactor The current ease factor
     * @param qualityRating The quality rating (0-5)
     * @return The new ease factor
     */
    private fun calculateNewEaseFactor(currentEaseFactor: Double, qualityRating: Int): Double {
        // SuperMemo-2 formula for adjusting the ease factor
        val newEaseFactor = currentEaseFactor + (0.1 - (5 - qualityRating) * (0.08 + (5 - qualityRating) * 0.02))

        // Ensure the ease factor doesn't go below the minimum
        return max(newEaseFactor, MIN_EASE_FACTOR)
    }
}
