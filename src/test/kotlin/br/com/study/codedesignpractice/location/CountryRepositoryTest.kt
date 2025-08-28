package br.com.study.codedesignpractice.location

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.repository.findByIdOrNull

@DataJpaTest
class CountryRepositoryTest @Autowired constructor(
    val entityManager: TestEntityManager,
    val countryRepository: CountryRepository
) {

    @Test
    fun `should be able to create a new country`() {
        val country = Country(name = "Brasil")

        entityManager.persist(country)
        entityManager.flush()

        val persistedCountry = countryRepository.findByIdOrNull(country.id!!)

        assertThat(persistedCountry).isEqualTo(country)
    }
}