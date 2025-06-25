package br.com.study.codedesignpractice.author

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import java.time.Instant
import java.util.*

@Entity
data class Author(
    var name: String,
    var email: String,
    var description: String,
    @Id @GeneratedValue var id: UUID? = null,
    var createdAt: Instant = Instant.now()
)
