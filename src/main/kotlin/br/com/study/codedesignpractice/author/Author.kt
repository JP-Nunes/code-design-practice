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
    @field:NotBlank val name: String,
    @field:NotBlank val email: String,
    @field:NotBlank @field:Size(max = 400) val description: String,
    @Id @GeneratedValue val id: UUID? = null,
    var createdAt: Instant = Instant.now()
)