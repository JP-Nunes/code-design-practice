package br.com.study.codedesignpractice.payment

import br.com.study.codedesignpractice.location.country.Country
import br.com.study.codedesignpractice.location.state.State
import io.mockk.every
import io.mockk.mockk
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity
import java.net.URI
import java.util.*

class PaymentControllerTest {

    private lateinit var paymentController: PaymentController
    private lateinit var paymentRepository: PaymentRepository
    private lateinit var entityManager: EntityManager
    private lateinit var stateBelongsToCountryValidator: StateBelongsToCountryValidator

    @BeforeEach
    fun setUp() {
        paymentRepository = mockk<PaymentRepository>()
        entityManager = mockk<EntityManager>()
        stateBelongsToCountryValidator = mockk<StateBelongsToCountryValidator>()
        paymentController = PaymentController(paymentRepository, entityManager, stateBelongsToCountryValidator)
    }

    @Test
    fun `should be able to register a new payment`() {
        val country = Country(name = "Argentina", id = UUID.randomUUID())
        val state = State(name = "Mendoza", country = country, id = UUID.randomUUID())
        every { entityManager.find(Country::class.java, country.id!!) } returns country
        every { entityManager.find(State::class.java, state.id!!) } returns state

        val createPaymentRequest = CreatePaymentRequest(
            email = "test@example.com",
            firstName = "John",
            lastName = "Doe",
            document = "12345678901",
            address = "123 Test Street",
            complement = "Apartment 456",
            city = "São Paulo",
            countryId = country.id,
            stateId = state.id,
            phone = "+5511987654321",
            zipcode = "01310-000",
            shoppingCart = CreatePaymentRequest.ShoppingCart(
                total = 100.0.toBigDecimal(),
                items = listOf(
                    shoppingCartItem(),
                    shoppingCartItem(quantity = 2),
                    shoppingCartItem(quantity = 20)
                )
            )
        )

        val payment = createPaymentRequest.toEntity(entityManager).copy(id = UUID.randomUUID())

        every { paymentRepository.save(any()) } returns payment

        val expected = ResponseEntity.created(URI("/v1/payments/${payment.id}")).body(CreatePaymentResponse.fromEntity(payment))
        val actual = paymentController.registerPayment(createPaymentRequest)

        assertThat(actual).isEqualTo(expected)
    }

    private fun shoppingCartItem(quantity: Int = 1) = CreatePaymentRequest.ShoppingCart.Item(UUID.randomUUID(), quantity = quantity)
}