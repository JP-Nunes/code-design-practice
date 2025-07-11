package br.com.study.codedesignpractice.category

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.validation.constraints.NotBlank
import java.util.UUID

@Entity
data class Category(
    @NotBlank val name: String,
    @Id @GeneratedValue val id: UUID? = null
)
