package me.maximilianmilz.vocabulary.infrastructure

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication(scanBasePackages = ["me.maximilianmilz.vocabulary"])
class VocabularyApiApplication

fun main(args: Array<String>) {
    runApplication<VocabularyApiApplication>(*args)
}