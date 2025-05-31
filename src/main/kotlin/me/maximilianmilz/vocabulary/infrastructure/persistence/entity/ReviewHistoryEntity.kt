package me.maximilianmilz.vocabulary.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * JPA entity for review history.
 */
@Entity
@Table(name = "review_history")
data class ReviewHistoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "vocabulary_entry_id", nullable = false)
    val vocabularyEntryId: Long,

    @Column(name = "review_date", nullable = false)
    val reviewDate: LocalDate,

    @Column(name = "quality_rating", nullable = false)
    val qualityRating: Int,

    @Column(name = "notes")
    val notes: String? = null,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime
)