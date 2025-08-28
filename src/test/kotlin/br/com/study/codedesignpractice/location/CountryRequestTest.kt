package br.com.study.codedesignpractice.location

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class CountryRequestTest {

    @Test
    fun `should be able to convert CountryRequest to entity`() {
        val countryRequest = CountryRequest("France")

        val expected = Country(name = countryRequest.name)
        val actual = countryRequest.toEntity()

        assertThat(expected).isEqualTo(actual)
    }
}