package me.maximilianmilz.vocabulary.api.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import me.maximilianmilz.vocabulary.api.dto.*
import me.maximilianmilz.vocabulary.api.exception.GlobalExceptionHandler
import me.maximilianmilz.vocabulary.application.service.BulkOperationService
import me.maximilianmilz.vocabulary.domain.model.Category
import me.maximilianmilz.vocabulary.domain.model.VocabularyEntry
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import java.time.LocalDate
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class VocabularyEntryBulkControllerTest {

    private lateinit var mockMvc: MockMvc

    @Mock
    private lateinit var bulkOperationService: BulkOperationService

    @InjectMocks
    private lateinit var controller: VocabularyEntryBulkController

    private lateinit var objectMapper: ObjectMapper
    private lateinit var testResponseDto: VocabularyEntryResponseDto
    private lateinit var testRequestDto: VocabularyEntryRequestDto
    private lateinit var bulkOperationResponse: BulkOperationResponseDto

    @BeforeEach
    fun setUp() {
        // Initialize ObjectMapper with JavaTimeModule for handling LocalDate
        objectMapper = ObjectMapper().registerModule(JavaTimeModule())

        // Set up MockMvc
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(GlobalExceptionHandler())
            .build()

        // Create test data
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

        testRequestDto = VocabularyEntryRequestDto(
            wordPt = "falar",
            wordDe = "sprechen",
            example = "Eu falo português.",
            level = 1,
            nextReview = LocalDate.now(),
            category = Category.VERBS
        )

        bulkOperationResponse = BulkOperationResponseDto(
            successCount = 2,
            failureCount = 0,
            failures = emptyList()
        )
    }

    @Test
    fun `bulkCreate should create multiple entries`() {
        // Given
        val request = BulkCreateRequestDto(
            entries = listOf(testRequestDto, testRequestDto)
        )

        `when`(bulkOperationService.bulkCreate(request)).thenReturn(bulkOperationResponse)

        // When/Then
        mockMvc.perform(post("/api/vocabulary-entries/bulk/create")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.successCount").value(bulkOperationResponse.successCount))
            .andExpect(jsonPath("$.failureCount").value(bulkOperationResponse.failureCount))
            .andExpect(jsonPath("$.failures").isArray)
    }

    @Test
    fun `bulkUpdate should update multiple entries`() {
        // Given
        val request = BulkUpdateRequestDto(
            entries = listOf(
                BulkUpdateEntryDto(
                    id = 1L,
                    entry = testRequestDto
                ),
                BulkUpdateEntryDto(
                    id = 2L,
                    entry = testRequestDto
                )
            )
        )

        `when`(bulkOperationService.bulkUpdate(request)).thenReturn(bulkOperationResponse)

        // When/Then
        mockMvc.perform(put("/api/vocabulary-entries/bulk/update")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.successCount").value(bulkOperationResponse.successCount))
            .andExpect(jsonPath("$.failureCount").value(bulkOperationResponse.failureCount))
            .andExpect(jsonPath("$.failures").isArray)
    }

    @Test
    fun `bulkDelete should delete multiple entries`() {
        // Given
        val request = BulkDeleteRequestDto(
            ids = listOf(1L, 2L)
        )

        `when`(bulkOperationService.bulkDelete(request)).thenReturn(bulkOperationResponse)

        // When/Then
        mockMvc.perform(delete("/api/vocabulary-entries/bulk/delete")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.successCount").value(bulkOperationResponse.successCount))
            .andExpect(jsonPath("$.failureCount").value(bulkOperationResponse.failureCount))
            .andExpect(jsonPath("$.failures").isArray)
    }

    @Test
    fun `exportEntries should return all entries`() {
        // Given
        val entries = listOf(testResponseDto, testResponseDto)

        `when`(bulkOperationService.exportEntries()).thenReturn(entries)

        // When/Then
        mockMvc.perform(get("/api/vocabulary-entries/bulk/export")
            .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$[0].id").value(testResponseDto.id))
            .andExpect(jsonPath("$[0].wordPt").value(testResponseDto.wordPt))
            .andExpect(jsonPath("$[0].wordDe").value(testResponseDto.wordDe))
            .andExpect(jsonPath("$[1].id").value(testResponseDto.id))
    }

    @Test
    fun `importEntries should import multiple entries`() {
        // Given
        val entries = listOf(testRequestDto, testRequestDto)

        `when`(bulkOperationService.importEntries(entries)).thenReturn(bulkOperationResponse)

        // When/Then
        mockMvc.perform(post("/api/vocabulary-entries/bulk/import")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(entries)))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.successCount").value(bulkOperationResponse.successCount))
            .andExpect(jsonPath("$.failureCount").value(bulkOperationResponse.failureCount))
            .andExpect(jsonPath("$.failures").isArray)
    }
}
