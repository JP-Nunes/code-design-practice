package br.com.study.codedesignpractice.location

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController
@RequestMapping("/v1/countries")
class CountryController(private val countryRepository: CountryRepository) {

    @PostMapping
    fun register(@RequestBody @Valid countryRequest: CountryRequest): ResponseEntity<CountryResponse> {
        val country = countryRequest.toEntity()
        val persistedCountry = countryRepository.save(country)
        return ResponseEntity
            .created(URI("/v1/countries/${persistedCountry.id}"))
            .body(CountryResponse.fromEntity(persistedCountry))
    }
}