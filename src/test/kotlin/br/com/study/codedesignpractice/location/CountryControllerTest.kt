package br.com.study.codedesignpractice.location

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity
import java.net.URI
import java.util.*

class CountryControllerTest {

    private lateinit var countryController: CountryController
    private lateinit var countryRepository: CountryRepository

    @BeforeEach
    fun setUp() {
        countryRepository = mockk<CountryRepository>()
        countryController = CountryController(countryRepository)
    }

    @Test
    fun `should be able to register a new country`() {
        val countryRequest = CountryRequest(name = "India")
        val country = countryRequest.toEntity()
        val persistedCountry = country.copy(id = UUID.randomUUID())

        every { countryRepository.save(country) } returns persistedCountry

        val expected = ResponseEntity
            .created(URI("/v1/countries/${persistedCountry.id}"))
            .body(CountryResponse.fromEntity(persistedCountry))
        val actual = countryController.register(countryRequest)

        assertThat(actual).isEqualTo(expected)
    }
}