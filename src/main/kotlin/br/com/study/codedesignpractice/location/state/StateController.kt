package br.com.study.codedesignpractice.location.state

import br.com.study.codedesignpractice.location.country.CountryRepository
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/v1/states")
class StateController(
    private val stateRepository: StateRepository,
    private val countryRepository: CountryRepository
) {

    @PostMapping
    fun register(@RequestBody @Valid createStateRequest: CreateStateRequest): ResponseEntity<CreateStateResponse> {
        val state = stateRepository.save(createStateRequest.toEntity(countryRepository))
        return ResponseEntity
            .created(java.net.URI("/v1/states/${state.id}"))
            .body(CreateStateResponse.fromEntity(state))
    }
}