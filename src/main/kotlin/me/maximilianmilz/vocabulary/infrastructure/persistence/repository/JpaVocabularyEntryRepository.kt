package me.maximilianmilz.vocabulary.infrastructure.persistence.repository

import me.maximilianmilz.vocabulary.domain.model.Category
import me.maximilianmilz.vocabulary.infrastructure.persistence.entity.VocabularyEntryEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDate

/**
 * Spring Data JPA repository for VocabularyEntryEntity.
 */
@Repository
interface JpaVocabularyEntryRepository : JpaRepository<VocabularyEntryEntity, Long> {
    /**
     * Find vocabulary entries by category.
     */
    fun findByCategory(category: String): List<VocabularyEntryEntity>

    /**
     * Find vocabulary entries due for review.
     */
    fun findByNextReviewBefore(date: LocalDate): List<VocabularyEntryEntity>

    /**
     * Find vocabulary entries by Portuguese word (case-insensitive, partial match).
     */
    fun findByWordPtContainingIgnoreCase(wordPt: String): List<VocabularyEntryEntity>

    /**
     * Find vocabulary entries by German word (case-insensitive, partial match).
     */
    fun findByWordDeContainingIgnoreCase(wordDe: String): List<VocabularyEntryEntity>

    /**
     * Find vocabulary entries by example text (case-insensitive, partial match).
     */
    fun findByExampleContainingIgnoreCase(example: String): List<VocabularyEntryEntity>

    /**
     * Find vocabulary entries by word in either Portuguese or German (case-insensitive, partial match).
     */
    @Query("SELECT v FROM VocabularyEntryEntity v WHERE LOWER(v.wordPt) LIKE LOWER(CONCAT('%', :word, '%')) OR LOWER(v.wordDe) LIKE LOWER(CONCAT('%', :word, '%'))")
    fun findByWordPtOrWordDe(@Param("word") word: String): List<VocabularyEntryEntity>
}
