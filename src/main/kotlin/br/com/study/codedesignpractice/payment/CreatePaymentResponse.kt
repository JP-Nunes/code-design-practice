package br.com.study.codedesignpractice.payment

import br.com.study.codedesignpractice.location.country.CountryResponse
import br.com.study.codedesignpractice.location.state.CreateStateResponse
import com.fasterxml.jackson.annotation.JsonInclude
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
data class CreatePaymentResponse(
    val id: UUID,
    val buyerName: String,
    val buyerLastName: String,
    val phone: String,
    val email: String,
    val address: String,
    val complement: String,
    val city: String,
    val country: CountryResponse,
    val state: CreateStateResponse?,
    val zipcode: String,
) {

    companion object {

        fun fromEntity(purchase: Purchase): CreatePaymentResponse {
            return with(purchase) {
                CreatePaymentResponse(
                    id = requireNotNull(this.id) { "Payment id cannot be null" },
                    buyerName = requireNotNull(this.firstName) { "First name cannot be null" },
                    buyerLastName = requireNotNull(this.lastName) { "Last name cannot be null" },
                    phone = requireNotNull(this.phone) { "Phone id cannot be null" },
                    email = requireNotNull(this.email) { "Email id cannot be null" },
                    address = requireNotNull(this.address) { "Address id cannot be null" },
                    city = requireNotNull(this.city) { "City id cannot be null" },
                    complement = requireNotNull(this.complement) { "Complement id cannot be null" },
                    country = CountryResponse.fromEntity(requireNotNull(this.country) { "Country id cannot be null" }),
                    state = this.state?.let { CreateStateResponse.fromEntity(it) },
                    zipcode = requireNotNull(this.zipcode) { "Zipcode id cannot be null" }
                )
            }
        }
    }
}
