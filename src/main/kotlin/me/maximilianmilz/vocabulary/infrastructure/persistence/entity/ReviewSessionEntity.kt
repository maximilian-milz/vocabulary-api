package me.maximilianmilz.vocabulary.infrastructure.persistence.entity

import jakarta.persistence.*
import me.maximilianmilz.vocabulary.domain.model.ReviewSessionStatus
import java.time.LocalDateTime

/**
 * JPA entity for review sessions.
 */
@Entity
@Table(name = "review_sessions")
data class ReviewSessionEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "start_time", nullable = false)
    val startTime: LocalDateTime,

    @Column(name = "end_time")
    val endTime: LocalDateTime? = null,

    @Column(name = "total_entries", nullable = false)
    val totalEntries: Int,

    @Column(name = "completed_entries", nullable = false)
    val completedEntries: Int = 0,

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    val status: ReviewSessionStatus = ReviewSessionStatus.IN_PROGRESS,

    @OneToMany(mappedBy = "session", cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
    val entries: List<ReviewSessionEntryEntity> = emptyList(),

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime
)

/**
 * JPA entity for review session entries.
 */
@Entity
@Table(name = "review_session_entries")
data class ReviewSessionEntryEntity(
    @EmbeddedId
    val id: ReviewSessionEntryId,

    @Column(name = "reviewed", nullable = false)
    val reviewed: Boolean = false,

    @Column(name = "quality_rating")
    val qualityRating: Int? = null,

    @Column(name = "review_time")
    val reviewTime: LocalDateTime? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("sessionId")
    @JoinColumn(name = "session_id")
    val session: ReviewSessionEntity? = null
)

/**
 * Composite primary key for ReviewSessionEntryEntity.
 */
@Embeddable
data class ReviewSessionEntryId(
    @Column(name = "session_id")
    val sessionId: Long,

    @Column(name = "vocabulary_entry_id")
    val vocabularyEntryId: Long
) : java.io.Serializable
