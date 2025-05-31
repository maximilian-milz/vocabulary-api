package me.maximilianmilz.vocabulary.api.controller

import me.maximilianmilz.vocabulary.application.service.StatisticsService
import me.maximilianmilz.vocabulary.domain.model.Category
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDate

/**
 * REST controller for vocabulary learning statistics.
 */
@RestController
@RequestMapping("/api/statistics")
class StatisticsController(
    private val statisticsService: StatisticsService
) {

    /**
     * Get overall learning progress statistics.
     * 
     * @return Overall statistics
     */
    @GetMapping("/overall")
    fun getOverallStatistics(): ResponseEntity<Map<String, Any?>> {
        val statistics = statisticsService.getOverallStatistics()
        return ResponseEntity.ok(statistics)
    }

    /**
     * Get statistics for a specific time period.
     * 
     * @param period The time period ('day', 'week', 'month', or 'year')
     * @return Period statistics
     */
    @GetMapping("/period/{period}")
    fun getPeriodStatistics(
        @PathVariable period: String
    ): ResponseEntity<Map<String, Any?>> {
        try {
            val statistics = statisticsService.getPeriodStatistics(period)
            return ResponseEntity.ok(statistics)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid period: $period. Valid values are 'day', 'week', 'month', or 'year'")
        }
    }

    /**
     * Get statistics for a specific category.
     * 
     * @param category The category ('VERBS', 'NOUNS', or 'ADJECTIVES')
     * @return Category statistics
     */
    @GetMapping("/category/{category}")
    fun getCategoryStatistics(
        @PathVariable category: Category
    ): ResponseEntity<Map<String, Any?>> {
        val statistics = statisticsService.getCategoryStatistics(category)
        return ResponseEntity.ok(statistics)
    }

    /**
     * Get statistics for a date range.
     * 
     * @param startDate The start date
     * @param endDate The end date
     * @return Date range statistics
     */
    @GetMapping("/range")
    fun getDateRangeStatistics(
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate,
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate
    ): ResponseEntity<Map<String, Any?>> {
        try {
            val statistics = statisticsService.getDateRangeStatistics(startDate, endDate)
            return ResponseEntity.ok(statistics)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("End date cannot be before start date")
        }
    }

    /**
     * Get statistics for all review sessions.
     * 
     * @return Session statistics
     */
    @GetMapping("/sessions")
    fun getSessionsStatistics(): ResponseEntity<List<Map<String, Any?>>> {
        val sessions = statisticsService.getSessionsStatistics()
        return ResponseEntity.ok(sessions)
    }

    /**
     * Get statistics for a specific review session.
     * 
     * @param sessionId The session ID
     * @return Session statistics
     */
    @GetMapping("/sessions/{sessionId}")
    fun getSessionStatistics(
        @PathVariable sessionId: Long
    ): ResponseEntity<Map<String, Any?>> {
        try {
            val session = statisticsService.getSessionStatistics(sessionId)
            return ResponseEntity.ok(session)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Review session not found with id: $sessionId")
        }
    }

    /**
     * Get learning curve data for visualization.
     * 
     * @param timeframe The timeframe ('day', 'week', 'month', 'year', or 'all')
     * @return Learning curve data
     */
    @GetMapping("/learning-curve/{timeframe}")
    fun getLearningCurveData(
        @PathVariable timeframe: String
    ): ResponseEntity<Map<String, Any?>> {
        try {
            val learningCurveData = statisticsService.getLearningCurveData(timeframe)
            return ResponseEntity.ok(learningCurveData)
        } catch (e: IllegalArgumentException) {
            throw IllegalArgumentException("Invalid timeframe: $timeframe. Valid values are 'day', 'week', 'month', 'year', or 'all'")
        }
    }

    /**
     * Get distribution of vocabulary entries by level.
     * 
     * @return Level distribution data
     */
    @GetMapping("/level-distribution")
    fun getLevelDistribution(): ResponseEntity<Map<String, Any?>> {
        val levelDistribution = statisticsService.getLevelDistribution()
        return ResponseEntity.ok(levelDistribution)
    }
    /**
     * Get review history statistics.
     * 
     * @param entryId Optional vocabulary entry ID to filter by
     * @param startDate Optional start date to filter by
     * @param endDate Optional end date to filter by
     * @return Review history statistics
     */
    @GetMapping("/review-history")
    fun getReviewHistoryStatistics(
        @RequestParam(required = false) entryId: Long?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) startDate: LocalDate?,
        @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) endDate: LocalDate?
    ): ResponseEntity<Map<String, Any?>> {
        val statistics = statisticsService.getReviewHistoryStatistics(entryId, startDate, endDate)
        return ResponseEntity.ok(statistics)
    }

    /**
     * Get daily review count statistics.
     * 
     * @param days Number of days to include (default 30)
     * @return Daily review count statistics
     */
    @GetMapping("/daily-review-count")
    fun getDailyReviewCountStatistics(
        @RequestParam(required = false, defaultValue = "30") days: Int
    ): ResponseEntity<Map<String, Any?>> {
        val statistics = statisticsService.getDailyReviewCountStatistics(days)
        return ResponseEntity.ok(statistics)
    }
}
