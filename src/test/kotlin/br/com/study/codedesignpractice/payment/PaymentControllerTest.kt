package br.com.study.codedesignpractice.payment

import br.com.study.codedesignpractice.author.Author
import br.com.study.codedesignpractice.book.repository.Book
import br.com.study.codedesignpractice.book.repository.findBooksByIds
import br.com.study.codedesignpractice.category.Category
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
import java.time.LocalDate
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

        val books = listOf(
            book(id = UUID.randomUUID()),
            book(id = UUID.randomUUID()),
            book(id = UUID.randomUUID())
        )
        every { entityManager.findBooksByIds(books.map { it.id!! }) } returns books

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
                items = books.map { shoppingCartItem(it.id!!) }
            )
        )

        val payment = createPaymentRequest.toEntity(entityManager)
        val persistedPayment = payment.copy(id = UUID.randomUUID())

        every { paymentRepository.save(payment) } returns persistedPayment

        val expected = ResponseEntity
            .created(URI("/v1/payments/${persistedPayment.id}"))
            .body(CreatePaymentResponse.fromEntity(persistedPayment))
        val actual = paymentController.registerPayment(createPaymentRequest)
        assertThat(actual).isEqualTo(expected)
    }

    private fun book(id: UUID): Book = Book(
        title = "Book One",
        summary = "First book summary",
        tableOfContents = "TOC 1",
        price = 300,
        numberOfPages = 200,
        isbn = "111-111-111",
        publishDate = LocalDate.now().plusDays(15),
        category = Category(name = "romance"),
        author = Author(
            name = "John Doe",
            email = "john.doe@outlook.com",
            description = "A lovely author full of love",
        ),
        id = id
    )

    private fun shoppingCartItem(bookId: UUID) =
        CreatePaymentRequest.ShoppingCart.Item(id = bookId, quantity = 2)
}