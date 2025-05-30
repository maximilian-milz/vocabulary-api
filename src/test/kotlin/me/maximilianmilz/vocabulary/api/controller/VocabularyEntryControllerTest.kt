package me.maximilianmilz.vocabulary.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import me.maximilianmilz.vocabulary.api.dto.ReviewResultDto
import me.maximilianmilz.vocabulary.api.dto.VocabularyEntryRequestDto
import me.maximilianmilz.vocabulary.api.dto.VocabularyEntryResponseDto
import me.maximilianmilz.vocabulary.api.exception.GlobalExceptionHandler
import me.maximilianmilz.vocabulary.api.exception.ResourceNotFoundException
import me.maximilianmilz.vocabulary.api.mapper.VocabularyEntryMapper
import me.maximilianmilz.vocabulary.application.service.SpacedRepetitionService
import me.maximilianmilz.vocabulary.domain.model.Category
import me.maximilianmilz.vocabulary.domain.model.VocabularyEntry
import me.maximilianmilz.vocabulary.domain.repository.VocabularyEntryRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class VocabularyEntryControllerTest {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var repository: VocabularyEntryRepository

    @Mock
    private lateinit var mapper: VocabularyEntryMapper

    @Mock
    private lateinit var spacedRepetitionService: SpacedRepetitionService

    @InjectMocks
    private lateinit var controller: VocabularyEntryController

    private lateinit var objectMapper: ObjectMapper
    private lateinit var testEntry: VocabularyEntry
    private lateinit var testResponseDto: VocabularyEntryResponseDto

    @BeforeEach
    fun setUp() {
        // Initialize ObjectMapper with JavaTimeModule for handling LocalDate
        objectMapper = ObjectMapper().registerModule(JavaTimeModule())

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
    fun `getAllEntries should return all entries`() {
        // Given
        val entries = listOf(testEntry)
        val responseDtos = listOf(testResponseDto)

        `when`(repository.findAll()).thenReturn(entries)
        `when`(mapper.toResponseDtoList(entries)).thenReturn(responseDtos)

        // When/Then
        mockMvc.perform(get("/api/vocabulary-entries")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(testResponseDto.id))
            .andExpect(jsonPath("$[0].wordPt").value(testResponseDto.wordPt))
            .andExpect(jsonPath("$[0].wordDe").value(testResponseDto.wordDe))
    }

    @Test
    fun `getEntryById should return entry when it exists`() {
        // Given
        val id = 1L

        `when`(repository.findById(id)).thenReturn(testEntry)
        `when`(mapper.toResponseDto(testEntry)).thenReturn(testResponseDto)

        // When/Then
        mockMvc.perform(get("/api/vocabulary-entries/{id}", id)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(testResponseDto.id))
            .andExpect(jsonPath("$.wordPt").value(testResponseDto.wordPt))
            .andExpect(jsonPath("$.wordDe").value(testResponseDto.wordDe))
    }

    @Test
    fun `getEntryById should return 404 when entry does not exist`() {
        // Given
        val id = 999L

        `when`(repository.findById(id)).thenReturn(null)

        // When/Then
        mockMvc.perform(get("/api/vocabulary-entries/{id}", id)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `createEntry should create and return new entry`() {
        // Given
        val requestDto = VocabularyEntryRequestDto(
            wordPt = "falar",
            wordDe = "sprechen",
            example = "Eu falo português.",
            level = 1,
            nextReview = LocalDate.now(),
            category = Category.VERBS
        )

        `when`(mapper.toDomainModel(requestDto)).thenReturn(testEntry)
        `when`(repository.save(testEntry)).thenReturn(testEntry)
        `when`(mapper.toResponseDto(testEntry)).thenReturn(testResponseDto)

        // When/Then
        mockMvc.perform(post("/api/vocabulary-entries")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isCreated)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(testResponseDto.id))
            .andExpect(jsonPath("$.wordPt").value(testResponseDto.wordPt))
            .andExpect(jsonPath("$.wordDe").value(testResponseDto.wordDe))
    }

    @Test
    fun `updateEntry should update and return entry when it exists`() {
        // Given
        val id = 1L
        val requestDto = VocabularyEntryRequestDto(
            wordPt = "falar",
            wordDe = "sprechen",
            example = "Eu falo português muito bem.",
            level = 2,
            nextReview = LocalDate.now().plusDays(1),
            category = Category.VERBS
        )

        val updatedEntry = testEntry.copy(
            example = "Eu falo português muito bem.",
            level = 2,
            nextReview = LocalDate.now().plusDays(1)
        )

        val updatedResponseDto = testResponseDto.copy(
            example = "Eu falo português muito bem.",
            level = 2,
            nextReview = LocalDate.now().plusDays(1)
        )

        `when`(repository.findById(id)).thenReturn(testEntry)
        `when`(mapper.toDomainModel(id, requestDto, testEntry.createdAt)).thenReturn(updatedEntry)
        `when`(repository.save(updatedEntry)).thenReturn(updatedEntry)
        `when`(mapper.toResponseDto(updatedEntry)).thenReturn(updatedResponseDto)

        // When/Then
        mockMvc.perform(put("/api/vocabulary-entries/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(updatedResponseDto.id))
            .andExpect(jsonPath("$.example").value(updatedResponseDto.example))
            .andExpect(jsonPath("$.level").value(updatedResponseDto.level))
    }

    @Test
    fun `updateEntry should return 404 when entry does not exist`() {
        // Given
        val id = 999L
        val requestDto = VocabularyEntryRequestDto(
            wordPt = "falar",
            wordDe = "sprechen",
            example = "Eu falo português muito bem.",
            level = 2,
            nextReview = LocalDate.now().plusDays(1),
            category = Category.VERBS
        )

        `when`(repository.findById(id)).thenReturn(null)

        // When/Then
        mockMvc.perform(put("/api/vocabulary-entries/{id}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(requestDto)))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `deleteEntry should delete entry when it exists`() {
        // Given
        val id = 1L

        `when`(repository.findById(id)).thenReturn(testEntry)
        doNothing().`when`(repository).deleteById(id)

        // When/Then
        mockMvc.perform(delete("/api/vocabulary-entries/{id}", id)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent)
    }

    @Test
    fun `deleteEntry should return 404 when entry does not exist`() {
        // Given
        val id = 999L

        `when`(repository.findById(id)).thenReturn(null)

        // When/Then
        mockMvc.perform(delete("/api/vocabulary-entries/{id}", id)
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isNotFound)
    }

    @Test
    fun `recordReviewResult should update and return entry when it exists`() {
        // Given
        val id = 1L
        val reviewResultDto = ReviewResultDto(qualityRating = 4)

        val updatedEntry = testEntry.copy(
            repetitions = 1,
            easeFactor = 2.6,
            nextReview = LocalDate.now().plusDays(1),
            lastReviewDate = LocalDate.now()
        )

        val updatedResponseDto = testResponseDto.copy(
            repetitions = 1,
            easeFactor = 2.6,
            nextReview = LocalDate.now().plusDays(1),
            lastReviewDate = LocalDate.now()
        )

        `when`(repository.findById(id)).thenReturn(testEntry)
        `when`(spacedRepetitionService.processReviewResult(testEntry, 4)).thenReturn(updatedEntry)
        `when`(repository.save(updatedEntry)).thenReturn(updatedEntry)
        `when`(mapper.toResponseDto(updatedEntry)).thenReturn(updatedResponseDto)

        // When/Then
        mockMvc.perform(post("/api/vocabulary-entries/{id}/review", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(reviewResultDto)))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.id").value(updatedResponseDto.id))
            .andExpect(jsonPath("$.repetitions").value(updatedResponseDto.repetitions))
            .andExpect(jsonPath("$.easeFactor").value(updatedResponseDto.easeFactor))
    }

    @Test
    fun `recordReviewResult should return 404 when entry does not exist`() {
        // Given
        val id = 999L
        val reviewResultDto = ReviewResultDto(qualityRating = 4)

        `when`(repository.findById(id)).thenReturn(null)

        // When/Then
        mockMvc.perform(post("/api/vocabulary-entries/{id}/review", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(reviewResultDto)))
            .andExpect(status().isNotFound)
    }
}
