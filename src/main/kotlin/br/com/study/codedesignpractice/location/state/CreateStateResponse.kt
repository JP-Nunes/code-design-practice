package br.com.study.codedesignpractice.location.state

import br.com.study.codedesignpractice.location.country.CountryResponse
import java.util.*

data class CreateStateResponse(
    val id: UUID,
    val name: String,
    val country: CountryResponse
) {

    companion object {

        fun fromEntity(state: State) = with(state) {
            val stateId = this.id ?: throw IllegalStateException("State id cannot be null")
            val stateName = this.name ?: throw IllegalStateException("State name cannot be null")
            val stateCountry = this.country ?: throw IllegalStateException("State country cannot be null")

            CreateStateResponse(
                id = stateId,
                name = stateName,
                country = CountryResponse.fromEntity(stateCountry)
            )
        }
    }

}
