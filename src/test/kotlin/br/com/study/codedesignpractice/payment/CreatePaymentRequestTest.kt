package br.com.study.codedesignpractice.payment

import br.com.study.codedesignpractice.author.Author
import br.com.study.codedesignpractice.book.repository.Book
import br.com.study.codedesignpractice.category.Category
import br.com.study.codedesignpractice.location.country.Country
import br.com.study.codedesignpractice.location.state.State
import io.mockk.every
import io.mockk.mockk
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
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
        val book = Book(
            title = "Book Title",
            summary = "Book summary",
            tableOfContents = "Markdown table of contents",
            price = 250,
            numberOfPages = 150,
            isbn = "123-456-789",
            publishDate = LocalDate.now().plusDays(10),
            category = Category(name = "Non Fiction"),
            author = Author(
                name = "John Doe",
                email = "john.doe@hotmail.com",
                description = "A sample author"
            ),
            id = UUID.randomUUID()
        )

        every { entityManager.find(Country::class.java, country.id!!) } returns country
        every { entityManager.find(State::class.java, state.id!!) } returns state
        every { entityManager.find(Book::class.java, book.id!!) } returns book

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
            zipcode = "18780-000",
            shoppingCart = CreatePaymentRequest.ShoppingCart(
                total = 100.0.toBigDecimal(),
                items = listOf(
                    shoppingCartItem(id = book.id!!),
                    shoppingCartItem(id = book.id!!, quantity = 2),
                    shoppingCartItem(id = book.id!!, quantity = 20)
                )
            )
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
                zipcode = this.zipcode,
                shoppingCart = Payment.ShoppingCart(
                    total = this.shoppingCart.total,
                    items =  this.shoppingCart.items?.map {
                        Payment.ShoppingCart.Item(book, it.quantity)
                    }
                )
            )
        }
        val actual = createPaymentRequest.toEntity(entityManager)

        assertThat(expected).isEqualTo(actual)
    }

    private fun shoppingCartItem(id: UUID, quantity: Int = 1) = CreatePaymentRequest.ShoppingCart.Item(bookId = id, quantity = quantity)
}