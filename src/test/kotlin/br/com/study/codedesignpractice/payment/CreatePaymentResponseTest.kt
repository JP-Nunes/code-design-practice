package br.com.study.codedesignpractice.payment

import br.com.study.codedesignpractice.location.country.Country
import br.com.study.codedesignpractice.location.country.CountryResponse
import br.com.study.codedesignpractice.location.state.CreateStateResponse
import br.com.study.codedesignpractice.location.state.State
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.util.UUID

class CreatePaymentResponseTest {

    @Test
    fun `should be able to generate a CreatePaymentResponse from Payment entity`() {
        val payment = payment()

        val expected = convertToCreatePaymentResponse(payment)
        val actual = CreatePaymentResponse.fromEntity(payment)

        assertThat(expected).isEqualTo(actual)
    }

    @Nested
    inner class FromEntityNullFieldsValidation {

        @Test
        fun `should validate if any payment field that should not be null is returning null when converting to CreatePaymentResponse`() {
            val nullFieldCases = listOf(
                { p: Payment -> p.copy(id = null) } to "Payment id cannot be null",
                { p: Payment -> p.copy(firstName = null) } to "First name cannot be null",
                { p: Payment -> p.copy(lastName = null) } to "Last name cannot be null",
                { p: Payment -> p.copy(phone = null) } to "Phone id cannot be null",
                { p: Payment -> p.copy(email = null) } to "Email id cannot be null",
                { p: Payment -> p.copy(address = null) } to "Address id cannot be null",
                { p: Payment -> p.copy(complement = null) } to "Complement id cannot be null",
                { p: Payment -> p.copy(city = null) } to "City id cannot be null",
                { p: Payment -> p.copy(country = null) } to "Country id cannot be null",
                { p: Payment -> p.copy(state = null) } to "State id cannot be null",
                { p: Payment -> p.copy(zipcode = null) } to "Zipcode id cannot be null"
            )

            val payment = payment()
            nullFieldCases.forEach { (paymentModifier, expectedMessage) ->
                val invalidPaymentWithNullField = paymentModifier(payment)

                val actualException = assertThrows<IllegalArgumentException> {
                    CreatePaymentResponse.fromEntity(invalidPaymentWithNullField)
                }

                assertThat(actualException.message).isEqualTo(expectedMessage)
            }
        }
    }

    private fun payment(): Payment {
        val country = Country(name = "Brazil", id = UUID.randomUUID())
        return Payment(
            email = "user.buyer@goodman.com",
            firstName = "Good",
            lastName = "Buyer",
            document = "08625390056",
            address = "Rua das Maritacas, 964",
            complement = "Apto 90 - Bloco B",
            city = "Rio de Janeiro",
            country = country,
            state = State(name = "Rio de Janeiro", country = country, id = UUID.randomUUID()),
            phone = "+5521988714077",
            zipcode = "01310-000",
            id = UUID.randomUUID()
        )
    }

    private fun convertToCreatePaymentResponse(payment: Payment): CreatePaymentResponse = with(payment) {
        CreatePaymentResponse(
            id = this.id!!,
            buyerName = this.firstName!!,
            buyerLastName = this.lastName!!,
            phone = this.phone!!,
            country = CountryResponse.fromEntity(this.country!!),
            email = this.email!!,
            address = this.address!!,
            city = this.city!!,
            state = CreateStateResponse.fromEntity(this.state!!),
            zipcode = this.zipcode!!,
            complement = this.complement!!
        )
    }
}