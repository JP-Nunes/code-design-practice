package br.com.study.codedesignpractice.location.state

import br.com.study.codedesignpractice.location.country.Country
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.repository.findByIdOrNull

@DataJpaTest
class StateRepositoryTest @Autowired constructor(
    val stateRepository: StateRepository,
    val entityManager: TestEntityManager
) {

    @Test
    fun `should be able to register a new state`() {
        val country = Country(name = "Brasil")
        entityManager.persist(country)

        val state = State(name = "São Paulo", country = country)
        entityManager.persist(state)
        entityManager.flush()

        val persistedState = stateRepository.findByIdOrNull(state.id!!)

        assertThat(state).isEqualTo(persistedState!!)
    }
}