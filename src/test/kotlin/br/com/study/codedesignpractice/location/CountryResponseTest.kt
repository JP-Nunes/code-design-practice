package br.com.study.codedesignpractice.location

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.*

class CountryResponseTest {

    @Test
    fun `should be able to create CountryResponse from Country entity`() {
        val country = Country(id = UUID.randomUUID(), name = "Brazil")

        val expected = with(country) { CountryResponse(id = this.id!!, name = this.name!!) }
        val actual = CountryResponse.fromEntity(country)

        assertThat(expected).isEqualTo(actual)
    }

    @Test
    fun `should be able to throw an IllegalStateException when converting CountryResponse from Country entity when id is null`() {
        val country = Country(id = null, name = "Brazil")

        val exception = assertThrows<IllegalStateException> {
            CountryResponse.fromEntity(country)
        }

        assertThat("Country id cannot be null").isEqualTo(exception.message)
    }

    @Test
    fun `should be able to throw an IllegalStateException when converting CountryResponse from Country entity when name is null`() {
        val country = Country(id = UUID.randomUUID(), name = null)

        val exception = assertThrows<IllegalStateException> {
            CountryResponse.fromEntity(country)
        }

        assertThat("Country name cannot be null").isEqualTo(exception.message)
    }
}