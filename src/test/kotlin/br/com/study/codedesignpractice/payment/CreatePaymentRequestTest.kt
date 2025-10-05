package br.com.study.codedesignpractice.payment

import br.com.study.codedesignpractice.location.country.Country
import br.com.study.codedesignpractice.location.state.State
import io.mockk.every
import io.mockk.mockk
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.UUID

class CreatePaymentRequestTest {

    private lateinit var entityManager: EntityManager

    @BeforeEach
    fun setUp() {
        entityManager = mockk<EntityManager>()
    }

    @Test
    fun `should be able to convert CreatePaymentRequest to Payment`() {
        val country = Country(name = "Argentina", id = UUID.randomUUID())
        val state = State(name = "Mendoza", country = country, id = UUID.randomUUID())

        every { entityManager.find(Country::class.java, country.id!!) } returns country
        every { entityManager.find(State::class.java, state.id!!) } returns state

        val createPaymentRequest = CreatePaymentRequest(
            email = "nice_user@goodpayer.com",
            firstName = "Johnny",
            lastName = "B. Good",
            document = "05984580004",
            address = "Rua das Carequentas, 725",
            complement = "Casa",
            city = "Mendoza City",
            countryId = country.id!!,
            stateId = state.id!!,
            phone = "+5511988714077",
            zipcode = "18780-000"
        )

        val expected = with(createPaymentRequest) {
            Payment(
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
        val actual = createPaymentRequest.toEntity(entityManager)

        assertThat(expected).isEqualTo(actual)
    }
}