package br.com.study.codedesignpractice.location.country

import br.com.study.codedesignpractice.validator.Unique
import jakarta.validation.constraints.NotBlank

data class CountryRequest(
    @field:NotBlank
    @field:Unique(message = "Country name must be unique", entityClass = Country::class, fieldName = "name")
    val name: String?
) {

    fun toEntity() = Country(name = this.name)
}
