package br.com.study.codedesignpractice.author

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.Instant
import java.util.*

@Entity
data class Author(
    @get:NotBlank var name: String,
    @get:NotBlank var email: String,
    @get:NotBlank @get:Size(max = 400) var description: String,
    @Id @GeneratedValue var id: UUID? = null,
    var createdAt: Instant = Instant.now()
)