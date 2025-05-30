package me.maximilianmilz.vocabulary.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import me.maximilianmilz.vocabulary.api.dto.VocabularyEntryResponseDto
import me.maximilianmilz.vocabulary.api.exception.GlobalExceptionHandler
import me.maximilianmilz.vocabulary.api.mapper.VocabularyEntryMapper
import me.maximilianmilz.vocabulary.domain.model.Category
import me.maximilianmilz.vocabulary.domain.model.VocabularyEntry
import me.maximilianmilz.vocabulary.domain.repository.VocabularyEntryRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class VocabularyEntrySearchControllerTest {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var repository: VocabularyEntryRepository

    @Mock
    private lateinit var mapper: VocabularyEntryMapper

    @InjectMocks
    private lateinit var controller: VocabularyEntrySearchController

    private lateinit var testEntry: VocabularyEntry
    private lateinit var testResponseDto: VocabularyEntryResponseDto

    @BeforeEach
    fun setUp() {
        // Set up MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(GlobalExceptionHandler())
            .build()

        // Create test data
        testEntry = VocabularyEntry(
            id = 1L,
            wordPt = "falar",
            wordDe = "sprechen",
            example = "Eu falo português.",
            level = 1,
            nextReview = LocalDate.now(),
            category = Category.VERBS,
            createdAt = LocalDateTime.now(),
            repetitions = 0,
            easeFactor = 2.5,
            lastReviewDate = null
        )

        testResponseDto = VocabularyEntryResponseDto(
            id = 1L,
            wordPt = "falar",
            wordDe = "sprechen",
            example = "Eu falo português.",
            level = 1,
            nextReview = LocalDate.now(),
            category = Category.VERBS,
            createdAt = LocalDateTime.now(),
            repetitions = 0,
            easeFactor = 2.5,
            lastReviewDate = null
        )
    }

    @Test
    fun `searchByWordPt should return matching entries`() {
        // Given
        val query = "falar"
        val entries = listOf(testEntry)
        val responseDtos = listOf(testResponseDto)

        `when`(repository.findByWordPt(query)).thenReturn(entries)
        `when`(mapper.toResponseDtoList(entries)).thenReturn(responseDtos)

        // When/Then
        mockMvc.perform(get("/api/vocabulary-entries/search/word-pt")
            .param("query", query)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(testResponseDto.id))
            .andExpect(jsonPath("$[0].wordPt").value(testResponseDto.wordPt))
    }

    @Test
    fun `searchByWordDe should return matching entries`() {
        // Given
        val query = "sprechen"
        val entries = listOf(testEntry)
        val responseDtos = listOf(testResponseDto)

        `when`(repository.findByWordDe(query)).thenReturn(entries)
        `when`(mapper.toResponseDtoList(entries)).thenReturn(responseDtos)

        // When/Then
        mockMvc.perform(get("/api/vocabulary-entries/search/word-de")
            .param("query", query)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(testResponseDto.id))
            .andExpect(jsonPath("$[0].wordDe").value(testResponseDto.wordDe))
    }

    @Test
    fun `searchByExample should return matching entries`() {
        // Given
        val query = "português"
        val entries = listOf(testEntry)
        val responseDtos = listOf(testResponseDto)

        `when`(repository.findByExample(query)).thenReturn(entries)
        `when`(mapper.toResponseDtoList(entries)).thenReturn(responseDtos)

        // When/Then
        mockMvc.perform(get("/api/vocabulary-entries/search/example")
            .param("query", query)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(testResponseDto.id))
            .andExpect(jsonPath("$[0].example").value(testResponseDto.example))
    }

    @Test
    fun `searchByWord should return matching entries`() {
        // Given
        val query = "falar"
        val entries = listOf(testEntry)
        val responseDtos = listOf(testResponseDto)

        `when`(repository.findByWord(query)).thenReturn(entries)
        `when`(mapper.toResponseDtoList(entries)).thenReturn(responseDtos)

        // When/Then
        mockMvc.perform(get("/api/vocabulary-entries/search/word")
            .param("query", query)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(testResponseDto.id))
            .andExpect(jsonPath("$[0].wordPt").value(testResponseDto.wordPt))
    }

    @Test
    fun `getEntriesByCategory should return entries for the specified category`() {
        // Given
        val category = Category.VERBS
        val entries = listOf(testEntry)
        val responseDtos = listOf(testResponseDto)

        `when`(repository.findByCategory(category)).thenReturn(entries)
        `when`(mapper.toResponseDtoList(entries)).thenReturn(responseDtos)

        // When/Then
        mockMvc.perform(get("/api/vocabulary-entries/search/category/{category}", category)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(testResponseDto.id))
            .andExpect(jsonPath("$[0].category").value(testResponseDto.category.toString()))
    }

    @Test
    fun `getEntriesDueForReview should return entries due for review`() {
        // Given
        val today = LocalDate.now()
        val entries = listOf(testEntry)
        val responseDtos = listOf(testResponseDto)

        `when`(repository.findByNextReviewBefore(today)).thenReturn(entries)
        `when`(mapper.toResponseDtoList(entries)).thenReturn(responseDtos)

        // When/Then
        mockMvc.perform(get("/api/vocabulary-entries/search/due")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(testResponseDto.id))
    }
}
