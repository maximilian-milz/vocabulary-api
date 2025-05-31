package me.maximilianmilz.vocabulary.application.service

import me.maximilianmilz.vocabulary.domain.model.Category
import me.maximilianmilz.vocabulary.domain.model.ReviewSessionStatus
import me.maximilianmilz.vocabulary.domain.repository.ReviewHistoryRepository
import me.maximilianmilz.vocabulary.domain.repository.ReviewSessionRepository
import me.maximilianmilz.vocabulary.domain.repository.VocabularyEntryRepository
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.temporal.ChronoUnit

/**
 * Implementation of the StatisticsService interface.
 */
@Service
class StatisticsServiceImpl(
    private val vocabularyEntryRepository: VocabularyEntryRepository,
    private val reviewHistoryRepository: ReviewHistoryRepository,
    private val reviewSessionRepository: ReviewSessionRepository
) : StatisticsService {

    /**
     * Get overall learning progress statistics.
     *
     * @return Map containing overall statistics
     */
    override fun getOverallStatistics(): Map<String, Any?> {
        val allEntries = vocabularyEntryRepository.findAll()

        val averageLevel = if (allEntries.isNotEmpty()) {
            allEntries.map { it.level }.average()
        } else {
            0.0
        }

        return mapOf(
            "totalEntries" to allEntries.size,
            "averageLevel" to averageLevel,
            "entriesByCategory" to allEntries.groupBy { it.category }.mapValues { it.value.size },
            "entriesDueToday" to vocabularyEntryRepository.findByNextReviewBefore(LocalDate.now()).size,
            "entriesDueTomorrow" to vocabularyEntryRepository.findByNextReviewBefore(LocalDate.now().plusDays(2))
                .filter { it.nextReview.isAfter(LocalDate.now()) }.size,
            "totalReviews" to reviewHistoryRepository.findAll().size,
            "completedSessions" to reviewSessionRepository.findByStatus(ReviewSessionStatus.COMPLETED).size
        )
    }

    /**
     * Get statistics for a specific time period.
     *
     * @param period The time period ('day', 'week', 'month', or 'year')
     * @return Map containing period statistics
     */
    override fun getPeriodStatistics(period: String): Map<String, Any?> {
        val today = LocalDate.now()

        val startDate = when (period.lowercase()) {
            "day" -> today
            "week" -> today.minusDays(today.dayOfWeek.value - 1L)
            "month" -> today.withDayOfMonth(1)
            "year" -> today.withDayOfYear(1)
            else -> throw IllegalArgumentException("Invalid period: $period. Valid values are 'day', 'week', 'month', or 'year'")
        }

        val allEntries = vocabularyEntryRepository.findAll()
        val entriesDueInPeriod = allEntries.filter { it.nextReview >= startDate && it.nextReview <= today }
        val reviewsInPeriod = reviewHistoryRepository.findByReviewDateBetween(startDate, today)

        return mapOf(
            "period" to period,
            "startDate" to startDate,
            "endDate" to today,
            "entriesDueInPeriod" to entriesDueInPeriod.size,
            "entriesByLevel" to entriesDueInPeriod.groupBy { it.level }.mapValues { it.value.size },
            "reviewsInPeriod" to reviewsInPeriod.size,
            "averageQualityRating" to if (reviewsInPeriod.isNotEmpty()) {
                reviewsInPeriod.map { it.qualityRating }.average()
            } else {
                0.0
            }
        )
    }

    /**
     * Get statistics for a specific category.
     *
     * @param category The category
     * @return Map containing category statistics
     */
    override fun getCategoryStatistics(category: Category): Map<String, Any?> {
        val entriesInCategory = vocabularyEntryRepository.findByCategory(category)

        val averageLevel = if (entriesInCategory.isNotEmpty()) {
            entriesInCategory.map { it.level }.average()
        } else {
            0.0
        }

        // Get review history for entries in this category
        val entryIds = entriesInCategory.mapNotNull { it.id }
        val reviewHistory = entryIds.flatMap { reviewHistoryRepository.findByVocabularyEntryId(it) }

        return mapOf(
            "category" to category,
            "totalEntries" to entriesInCategory.size,
            "averageLevel" to averageLevel,
            "entriesByLevel" to entriesInCategory.groupBy { it.level }.mapValues { it.value.size },
            "entriesDueToday" to entriesInCategory.count { it.nextReview <= LocalDate.now() },
            "totalReviews" to reviewHistory.size,
            "averageQualityRating" to if (reviewHistory.isNotEmpty()) {
                reviewHistory.map { it.qualityRating }.average()
            } else {
                0.0
            }
        )
    }

    /**
     * Get statistics for a date range.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return Map containing date range statistics
     */
    override fun getDateRangeStatistics(startDate: LocalDate, endDate: LocalDate): Map<String, Any?> {
        if (endDate.isBefore(startDate)) {
            throw IllegalArgumentException("End date cannot be before start date")
        }

        val allEntries = vocabularyEntryRepository.findAll()
        val entriesDueInRange = allEntries.filter { it.nextReview >= startDate && it.nextReview <= endDate }
        val reviewsInRange = reviewHistoryRepository.findByReviewDateBetween(startDate, endDate)

        return mapOf(
            "startDate" to startDate,
            "endDate" to endDate,
            "daysBetween" to ChronoUnit.DAYS.between(startDate, endDate) + 1,
            "entriesDueInRange" to entriesDueInRange.size,
            "entriesByDay" to entriesDueInRange.groupBy { it.nextReview }
                .mapValues { it.value.size }
                .toSortedMap(),
            "reviewsInRange" to reviewsInRange.size,
            "reviewsByDay" to reviewsInRange.groupBy { it.reviewDate }
                .mapValues { it.value.size }
                .toSortedMap(),
            "averageQualityRating" to if (reviewsInRange.isNotEmpty()) {
                reviewsInRange.map { it.qualityRating }.average()
            } else {
                0.0
            }
        )
    }

    /**
     * Get statistics for all review sessions.
     *
     * @return List of maps containing session statistics
     */
    override fun getSessionsStatistics(): List<Map<String, Any?>> {
        return reviewSessionRepository.findAll().map { session ->
            val entries = session.entries
            val reviewedEntries = entries.filter { it.reviewed }
            val averageQuality = if (reviewedEntries.isNotEmpty()) {
                reviewedEntries.mapNotNull { it.qualityRating }.average()
            } else {
                0.0
            }

            mapOf(
                "sessionId" to session.id,
                "startTime" to session.startTime,
                "endTime" to session.endTime,
                "totalEntries" to session.totalEntries,
                "completedEntries" to session.completedEntries,
                "status" to session.status,
                "averageQuality" to averageQuality
            )
        }
    }

    /**
     * Get statistics for a specific review session.
     *
     * @param sessionId The session ID
     * @return Map containing session statistics
     */
    override fun getSessionStatistics(sessionId: Long): Map<String, Any?> {
        val session = reviewSessionRepository.findById(sessionId)
            ?: throw IllegalArgumentException("Review session not found with id: $sessionId")

        val entries = session.entries
        val reviewedEntries = entries.filter { it.reviewed }
        val entriesByQuality = reviewedEntries
            .mapNotNull { it.qualityRating }
            .groupBy { it }
            .mapValues { it.value.size }
            .toSortedMap()

        // Fill in missing quality ratings with 0
        val completeEntriesByQuality = (0..5).associateWith { quality ->
            entriesByQuality[quality] ?: 0
        }

        val averageQuality = if (reviewedEntries.isNotEmpty()) {
            reviewedEntries.mapNotNull { it.qualityRating }.average()
        } else {
            0.0
        }

        return mapOf(
            "sessionId" to session.id,
            "startTime" to session.startTime,
            "endTime" to session.endTime,
            "totalEntries" to session.totalEntries,
            "completedEntries" to session.completedEntries,
            "status" to session.status,
            "entriesByQuality" to completeEntriesByQuality,
            "averageQuality" to averageQuality
        )
    }

    /**
     * Get learning curve data for visualization.
     *
     * @param timeframe The timeframe ('day', 'week', 'month', 'year', or 'all')
     * @return Map containing learning curve data
     */
    override fun getLearningCurveData(timeframe: String): Map<String, Any?> {
        val today = LocalDate.now()

        val startDate = when (timeframe.lowercase()) {
            "day" -> today
            "week" -> today.minusDays(7)
            "month" -> today.minusMonths(1)
            "year" -> today.minusYears(1)
            "all" -> today.minusYears(10) // Arbitrary past date
            else -> throw IllegalArgumentException("Invalid timeframe: $timeframe. Valid values are 'day', 'week', 'month', 'year', or 'all'")
        }

        // Get all review history in the timeframe
        val reviewHistory = reviewHistoryRepository.findByReviewDateBetween(startDate, today)

        // Group by date and calculate average level
        val dataPoints = reviewHistory
            .groupBy { it.reviewDate }
            .mapValues { entry ->
                val entryIds = entry.value.map { it.vocabularyEntryId }.distinct()
                val entries = entryIds.mapNotNull { vocabularyEntryRepository.findById(it) }
                val averageLevel = if (entries.isNotEmpty()) {
                    entries.map { it.level }.average()
                } else {
                    0.0
                }
                mapOf(
                    "date" to entry.key,
                    "averageLevel" to averageLevel,
                    "reviewCount" to entry.value.size
                )
            }
            .values
            .sortedBy { it["date"] as LocalDate }

        return mapOf(
            "timeframe" to timeframe,
            "startDate" to startDate,
            "endDate" to today,
            "dataPoints" to dataPoints
        )
    }

    /**
     * Get distribution of vocabulary entries by level.
     *
     * @return Map containing level distribution data
     */
    override fun getLevelDistribution(): Map<String, Any?> {
        val allEntries = vocabularyEntryRepository.findAll()
        val entriesByLevel = allEntries.groupBy { it.level }.mapValues { it.value.size }

        return mapOf(
            "totalEntries" to allEntries.size,
            "entriesByLevel" to entriesByLevel,
            "percentageByLevel" to entriesByLevel.mapValues { 
                (it.value.toDouble() / allEntries.size) * 100 
            }
        )
    }

    /**
     * Get review history statistics.
     *
     * @param entryId Optional vocabulary entry ID to filter by
     * @param startDate Optional start date to filter by
     * @param endDate Optional end date to filter by
     * @return Map containing review history statistics
     */
    override fun getReviewHistoryStatistics(entryId: Long?, startDate: LocalDate?, endDate: LocalDate?): Map<String, Any?> {
        // Get review history based on filters
        val reviewHistory = when {
            entryId != null && startDate != null && endDate != null -> {
                reviewHistoryRepository.findByVocabularyEntryId(entryId)
                    .filter { it.reviewDate >= startDate && it.reviewDate <= endDate }
            }
            entryId != null -> {
                reviewHistoryRepository.findByVocabularyEntryId(entryId)
            }
            startDate != null && endDate != null -> {
                reviewHistoryRepository.findByReviewDateBetween(startDate, endDate)
            }
            else -> {
                reviewHistoryRepository.findAll()
            }
        }

        // Calculate statistics
        val qualityDistribution = reviewHistory
            .groupBy { it.qualityRating }
            .mapValues { it.value.size }
            .toSortedMap()

        // Fill in missing quality ratings with 0
        val completeQualityDistribution = (0..5).associateWith { quality ->
            qualityDistribution[quality] ?: 0
        }

        val averageQuality = if (reviewHistory.isNotEmpty()) {
            reviewHistory.map { it.qualityRating }.average()
        } else {
            0.0
        }

        return mapOf(
            "totalReviews" to reviewHistory.size,
            "qualityDistribution" to completeQualityDistribution,
            "averageQuality" to averageQuality,
            "reviewsByDate" to reviewHistory.groupBy { it.reviewDate }
                .mapValues { it.value.size }
                .toSortedMap()
        )
    }

    /**
     * Get daily review count statistics.
     *
     * @param days Number of days to include (default 30)
     * @return Map containing daily review count statistics
     */
    override fun getDailyReviewCountStatistics(days: Int): Map<String, Any?> {
        val endDate = LocalDate.now()
        val startDate = endDate.minusDays(days.toLong() - 1)

        // Get all review history in the date range
        val reviewHistory = reviewHistoryRepository.findByReviewDateBetween(startDate, endDate)

        // Create a map with all dates in the range, initialized with 0 counts
        val dateRange = (0 until days).map { startDate.plusDays(it.toLong()) }
        val dailyCounts = dateRange.associateWith { 0 }.toMutableMap()

        // Fill in actual counts
        reviewHistory.forEach { 
            dailyCounts[it.reviewDate] = (dailyCounts[it.reviewDate] ?: 0) + 1
        }

        // Calculate statistics
        val totalReviews = reviewHistory.size
        val averageDailyReviews = if (days > 0) totalReviews.toDouble() / days else 0.0
        val maxDailyReviews = dailyCounts.values.maxOrNull() ?: 0

        return mapOf(
            "startDate" to startDate,
            "endDate" to endDate,
            "days" to days,
            "totalReviews" to totalReviews,
            "averageDailyReviews" to averageDailyReviews,
            "maxDailyReviews" to maxDailyReviews,
            "dailyCounts" to dailyCounts.toSortedMap()
        )
    }
}