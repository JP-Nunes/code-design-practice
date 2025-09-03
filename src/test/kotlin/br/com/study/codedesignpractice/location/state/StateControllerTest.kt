package br.com.study.codedesignpractice.location.state

import br.com.study.codedesignpractice.location.country.Country
import br.com.study.codedesignpractice.location.country.CountryRepository
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import java.net.URI
import java.util.*

class StateControllerTest {

    private lateinit var stateController: StateController
    private lateinit var stateRepository: StateRepository
    private lateinit var countryRepository: CountryRepository

    @BeforeEach
    fun setUp() {
        stateRepository = mockk<StateRepository>()
        countryRepository = mockk<CountryRepository>()
        stateController = StateController(stateRepository, countryRepository)
    }

    @Test
    fun `should be able to register a new state`() {
        val country = Country(name = "Ecuador", id = UUID.randomUUID())
        val createStateRequest = CreateStateRequest(name = "Quito", countryId = country.id)

        every { countryRepository.findByIdOrNull(country.id!!) } returns country

        val state = createStateRequest.toEntity(countryRepository)
        val persistedState = state.copy(id = UUID.randomUUID())
        every { stateRepository.save(state) } returns persistedState

        val expected = ResponseEntity
            .created(URI("/v1/states/${persistedState.id}"))
            .body(CreateStateResponse.fromEntity(persistedState))
        val actual = stateController.register(createStateRequest)

        assertThat(actual).isEqualTo(expected)
    }
}