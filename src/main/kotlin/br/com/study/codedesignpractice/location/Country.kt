package br.com.study.codedesignpractice.location

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.validation.constraints.NotBlank
import java.util.*

@Entity
data class Country(
    @field:NotBlank val name: String?,
    @Id @field:GeneratedValue var id: UUID? = null
)
