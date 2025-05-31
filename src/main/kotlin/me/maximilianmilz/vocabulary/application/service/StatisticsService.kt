package me.maximilianmilz.vocabulary.application.service

import me.maximilianmilz.vocabulary.domain.model.Category
import java.time.LocalDate

/**
 * Service interface for vocabulary learning statistics.
 */
interface StatisticsService {
    /**
     * Get overall learning progress statistics.
     *
     * @return Map containing overall statistics
     */
    fun getOverallStatistics(): Map<String, Any?>

    /**
     * Get statistics for a specific time period.
     *
     * @param period The time period ('day', 'week', 'month', or 'year')
     * @return Map containing period statistics
     */
    fun getPeriodStatistics(period: String): Map<String, Any?>

    /**
     * Get statistics for a specific category.
     *
     * @param category The category
     * @return Map containing category statistics
     */
    fun getCategoryStatistics(category: Category): Map<String, Any?>

    /**
     * Get statistics for a date range.
     *
     * @param startDate The start date
     * @param endDate The end date
     * @return Map containing date range statistics
     */
    fun getDateRangeStatistics(startDate: LocalDate, endDate: LocalDate): Map<String, Any?>

    /**
     * Get statistics for all review sessions.
     *
     * @return List of maps containing session statistics
     */
    fun getSessionsStatistics(): List<Map<String, Any?>>

    /**
     * Get statistics for a specific review session.
     *
     * @param sessionId The session ID
     * @return Map containing session statistics
     */
    fun getSessionStatistics(sessionId: Long): Map<String, Any?>

    /**
     * Get learning curve data for visualization.
     *
     * @param timeframe The timeframe ('day', 'week', 'month', 'year', or 'all')
     * @return Map containing learning curve data
     */
    fun getLearningCurveData(timeframe: String): Map<String, Any?>

    /**
     * Get distribution of vocabulary entries by level.
     *
     * @return Map containing level distribution data
     */
    fun getLevelDistribution(): Map<String, Any?>

    /**
     * Get review history statistics.
     *
     * @param entryId Optional vocabulary entry ID to filter by
     * @param startDate Optional start date to filter by
     * @param endDate Optional end date to filter by
     * @return Map containing review history statistics
     */
    fun getReviewHistoryStatistics(entryId: Long? = null, startDate: LocalDate? = null, endDate: LocalDate? = null): Map<String, Any?>

    /**
     * Get daily review count statistics.
     *
     * @param days Number of days to include (default 30)
     * @return Map containing daily review count statistics
     */
    fun getDailyReviewCountStatistics(days: Int = 30): Map<String, Any?>
}