package me.maximilianmilz.vocabulary.domain.model

import io.swagger.v3.oas.annotations.media.Schema

/**
 * Enum representing the category of a vocabulary entry.
 */
@Schema(enumAsRef = true)
enum class Category {
    VERBS,
    NOUNS,
    ADJECTIVES
}