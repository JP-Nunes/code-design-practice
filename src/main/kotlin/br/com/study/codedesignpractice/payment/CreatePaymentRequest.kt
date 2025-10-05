package br.com.study.codedesignpractice.payment

import br.com.study.codedesignpractice.location.country.Country
import br.com.study.codedesignpractice.location.state.State
import br.com.study.codedesignpractice.validator.CpfCnpj
import br.com.study.codedesignpractice.validator.Exists
import jakarta.persistence.EntityManager
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.util.*

data class CreatePaymentRequest(
    @field:NotBlank
    @field:Email
    val email: String?,

    @field:NotBlank
    val firstName: String?,

    @field:NotBlank
    val lastName: String?,

    @field:NotBlank
    @field:CpfCnpj
    val document: String?,

    @field:NotBlank
    val address: String?,

    @field:NotBlank
    val complement: String?,

    @field:NotBlank
    val city: String?,

    @field:NotNull
    @field:Exists(entityClass = Country::class, fieldName = "id")
    val countryId: UUID?,

    @field:Exists(entityClass = State::class, fieldName = "id")
    val stateId: UUID?,

    @field:NotBlank
    val zipcode: String?,

    @field:NotBlank
    val phone: String?,
) {

    fun toEntity(entityManager: EntityManager): Payment {
        val country = requireNotNull(entityManager.find(Country::class.java, this.countryId)) { "Country not found" }
        val state = requireNotNull(entityManager.find(State::class.java, this.stateId)) { "State not found" }

        return Payment(
            email = this.email,
            firstName = this.firstName,
            lastName = this.lastName,
            document = this.document,
            address = this.address,
            complement = this.complement,
            city = this.city,
            country = country,
            state = state,
            phone = this.phone,
            zipcode = this.zipcode
        )
    }
}
