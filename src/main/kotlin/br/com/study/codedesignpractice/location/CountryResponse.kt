package br.com.study.codedesignpractice.location

import java.util.UUID

data class CountryResponse(val id: UUID, val name: String) {

    companion object {

        fun fromEntity(country: Country): CountryResponse {
            val countryId = country.id ?: throw IllegalStateException("Country id cannot be null")
            val countryName = country.name ?: throw IllegalStateException("Country name cannot be null")

            return CountryResponse(countryId, countryName)
        }
    }
}
