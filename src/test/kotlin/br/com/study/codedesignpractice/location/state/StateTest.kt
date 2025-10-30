package br.com.study.codedesignpractice.location.state

import br.com.study.codedesignpractice.location.country.Country
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StateTest {

    @Test
    fun `should be able to verify if a state belongs to a country`() {
        val country = Country(name = "Brasil")
        val state = State(name = "Rio de Janeiro", country = country)

        assertThat(state.belongsTo(country)).isTrue()
    }

    @Test
    fun `should be able to verify if a state does not belongs to a country`() {
        val country = Country(name = "Brasil")
        val state = State(name = "Rio de Janeiro", country = country)
        val anotherCountry = Country(name = "Ecuador")

        assertThat(state.belongsTo(anotherCountry)).isFalse()
    }
}