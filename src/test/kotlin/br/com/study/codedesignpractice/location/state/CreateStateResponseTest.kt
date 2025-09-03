package br.com.study.codedesignpractice.location.state

import br.com.study.codedesignpractice.location.country.Country
import br.com.study.codedesignpractice.location.country.CountryResponse
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class CreateStateResponseTest {

    @Test
    fun `should be able to create a CreateStateResponse from State entity`() {
        val country = Country(name = "Japan", id = UUID.randomUUID())
        val state = State(name = "Tokyo", country = country, id = UUID.randomUUID())

        val expected = with(state) {
            CreateStateResponse(
                id = this.id!!,
                name = this.name!!,
                country = CountryResponse.fromEntity(country)
            )
        }
        val actual = CreateStateResponse.fromEntity(state)

        assertThat(expected).isEqualTo(actual)
    }

    @Test
    fun `should throw IllegalStateException trying to convert CreateStateResponse from State entity with null id`() {
        val country = Country(name = "Japan", id = UUID.randomUUID())
        val state = State(name = "Tokyo", country = country, id = null)

        val exception = assertThrows<IllegalStateException> { CreateStateResponse.fromEntity(state) }

        assertThat("State id cannot be null").isEqualTo(exception.message)
    }

    @Test
    fun `should throw IllegalStateException trying to convert CreateStateResponse from State entity with null name`() {
        val country = Country(name = "Japan", id = UUID.randomUUID())
        val state = State(name = null, country = country, id = UUID.randomUUID())

        val exception = assertThrows<IllegalStateException> { CreateStateResponse.fromEntity(state) }

        assertThat("State name cannot be null").isEqualTo(exception.message)
    }

    @Test
    fun `should throw IllegalStateException trying to convert CreateStateResponse from State entity with null country`() {
        val state = State(name = "Tokyo", country = null, id = UUID.randomUUID())
        val exception = assertThrows<IllegalStateException> { CreateStateResponse.fromEntity(state) }
        assertThat("State country cannot be null").isEqualTo(exception.message)
    }
}