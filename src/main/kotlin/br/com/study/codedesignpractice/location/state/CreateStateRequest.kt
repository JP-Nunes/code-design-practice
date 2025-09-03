package br.com.study.codedesignpractice.location.state

import br.com.study.codedesignpractice.location.country.Country
import br.com.study.codedesignpractice.location.country.CountryRepository
import br.com.study.codedesignpractice.validator.Exists
import br.com.study.codedesignpractice.validator.Unique
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.springframework.data.repository.findByIdOrNull
import java.util.*

data class CreateStateRequest(
    @field:NotBlank
    @field:Unique(message = "State name must be unique", entityClass = State::class, fieldName = "name")
    val name: String?,

    @field:NotNull
    @field:Exists(entityClass = Country::class, fieldName = "id")
    val countryId: UUID?
) {

    fun toEntity(countryRepository: CountryRepository): State {
        val countryId = this.countryId ?: throw IllegalArgumentException("Country id cannot be null")
        val country = countryRepository.findByIdOrNull(countryId) ?: throw IllegalArgumentException("Country was not found")
        return State(name = this.name, country = country)
    }
}
