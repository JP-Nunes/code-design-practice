package br.com.study.codedesignpractice.location.state

import br.com.study.codedesignpractice.location.country.Country
import br.com.study.codedesignpractice.location.country.CountryRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.data.repository.findByIdOrNull
import java.util.UUID

class CreateStateRequestTest {

    private lateinit var countryRepository: CountryRepository

    @BeforeEach
    fun setUp() {
        countryRepository = mockk<CountryRepository>()
    }

    @Test
    fun `should be able to convert CreateStateRequest to entity`() {
        val countryId = UUID.randomUUID()
        val country = Country(name = "France", id = countryId)
        every { countryRepository.findByIdOrNull(countryId) } returns country

        val crateStateRequest = CreateStateRequest(name = "Paris", countryId = countryId)
        val expected = State(name = crateStateRequest.name, country = country)
        val actual = crateStateRequest.toEntity(countryRepository)

        assertThat(expected).isEqualTo(actual)
    }

    @Test
    fun `should throw an IllegalStateException when converting CreateStateRequest to entity when country is null`() {
        val crateStateRequest = CreateStateRequest(name = "Paris", countryId = null)
        val exception = assertThrows<IllegalArgumentException> { crateStateRequest.toEntity(countryRepository) }

        assertThat("Country id cannot be null").isEqualTo(exception.message)
    }

    @Test
    fun `should throw an IllegalStateException when converting CreateStateRequest to entity when country is not found in database`() {
        val countryId = UUID.randomUUID()
        every { countryRepository.findByIdOrNull(countryId) } returns null

        val crateStateRequest = CreateStateRequest(name = "Paris", countryId = countryId)
        val exception = assertThrows<IllegalArgumentException> { crateStateRequest.toEntity(countryRepository) }

        assertThat("Country was not found").isEqualTo(exception.message)
    }
}