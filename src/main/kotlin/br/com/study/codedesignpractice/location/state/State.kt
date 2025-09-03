package br.com.study.codedesignpractice.location.state

import br.com.study.codedesignpractice.location.country.Country
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.*

@Entity
data class State(
    @field:NotBlank
    val name: String?,

    @field:NotNull
    @field:ManyToOne
    val country: Country?,

    @Id
    @field:GeneratedValue
    val id: UUID? = null
)