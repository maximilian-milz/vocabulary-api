package me.maximilianmilz.vocabulary.infrastructure.persistence.entity

import jakarta.persistence.*
import java.time.LocalDate
import java.time.LocalDateTime

/**
 * JPA entity for vocabulary entries.
 */
@Entity
@Table(name = "vocabulary_entries")
class VocabularyEntryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    @Column(nullable = false)
    val wordPt: String,
    
    @Column(nullable = false)
    val wordDe: String,
    
    @Column(nullable = false)
    val example: String,
    
    @Column(nullable = false)
    val level: Int,
    
    @Column(name = "next_review", nullable = false)
    val nextReview: LocalDate,
    
    @Column(nullable = false)
    val category: String,
    
    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime
)