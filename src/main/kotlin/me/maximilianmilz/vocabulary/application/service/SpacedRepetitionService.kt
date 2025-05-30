package me.maximilianmilz.vocabulary.application.service

import me.maximilianmilz.vocabulary.domain.model.VocabularyEntry
import java.time.LocalDate

/**
 * Service interface for spaced repetition algorithm.
 */
interface SpacedRepetitionService {
    /**
     * Process a review result for a vocabulary entry and calculate the next review date.
     *
     * @param entry The vocabulary entry being reviewed
     * @param qualityRating The quality of the response (0-5):
     *   0 - Complete blackout, no recognition
     *   1 - Incorrect response, but recognized the word
     *   2 - Incorrect response, but upon seeing the correct answer it felt familiar
     *   3 - Correct response, but required significant effort to recall
     *   4 - Correct response, after some hesitation
     *   5 - Correct response, perfect recall
     * @return The updated vocabulary entry with new review schedule
     */
    fun processReviewResult(entry: VocabularyEntry, qualityRating: Int): VocabularyEntry
    
    /**
     * Calculate the next review date based on the spaced repetition algorithm.
     *
     * @param repetitions Number of consecutive correct reviews
     * @param easeFactor The ease factor for the entry
     * @param previousInterval The previous interval in days
     * @return The next review date
     */
    fun calculateNextReviewDate(repetitions: Int, easeFactor: Double, previousInterval: Int): LocalDate
}