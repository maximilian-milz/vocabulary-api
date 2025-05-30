package me.maximilianmilz.vocabulary.infrastructure.config

import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.info.License
import io.swagger.v3.oas.models.servers.Server
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * Configuration for OpenAPI/Swagger documentation using Kotlin's apply functions.
 */
@Configuration
class OpenApiConfig {

    @Bean
    fun openAPI(): OpenAPI = OpenAPI().apply {
        info = createInfo()
        servers = listOf(
            Server().apply {
                url = "/"
                description = "Default Server URL"
            }
        )
    }

    private fun createInfo(): Info = Info().apply {
        title = "Vocabulary API"
        description = "REST API for managing vocabulary items"
        version = "v1.0.0"
        contact = Contact().apply {
            name = "Maximilian Milz"
            url = "https://github.com/maximilianmilz"
            email = "info@maximilian-milz.me"
        }
        license = License().apply {
            name = "MIT License"
            url = "https://opensource.org/licenses/MIT"
        }
    }
}
