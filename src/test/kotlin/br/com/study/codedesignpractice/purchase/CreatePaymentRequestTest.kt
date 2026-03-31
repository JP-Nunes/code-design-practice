package br.com.study.codedesignpractice.purchase

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
import java.time.LocalDate
import java.util.*
import kotlin.collections.sumOf

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
        val books = listOf(book(), book(), book())

        every { entityManager.find(Country::class.java, country.id!!) } returns country
        every { entityManager.find(State::class.java, state.id!!) } returns state
        every { entityManager.findBooksByIds(books.map { it.id!! }) } returns books

        val createPurchaseRequest = createPurchaseRequest(
            country = country,
            state = state,
            shoppingCart = shoppingCartRequest(books)
        )

        val expected = with(createPurchaseRequest) {
            Purchase(
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
                shoppingCart = Purchase.ShoppingCart(
                    total = books.sumOf { it.price!! },
                    items =  shoppingCart!!.items!!.map { bookRequest ->
                        Purchase.ShoppingCart.Item(
                            book = books.find { it.id == bookRequest.id },
                            bookRequest.quantity
                        )
                    }
                )
            )
        }
        val actual = createPurchaseRequest.toEntity(entityManager)

        assertThat(expected).isEqualTo(actual)
    }

    private fun book(): Book = Book(
        title = "Book Title",
        summary = "Book summary",
        tableOfContents = "Markdown table of contents",
        price = 250.toBigDecimal(),
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

    private fun createPurchaseRequest(
        country: Country,
        state: State,
        shoppingCart: CreatePurchaseRequest.ShoppingCart?
    ): CreatePurchaseRequest = CreatePurchaseRequest(
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
        shoppingCart = shoppingCart
    )

    private fun shoppingCartRequest(books: List<Book>): CreatePurchaseRequest.ShoppingCart = CreatePurchaseRequest.ShoppingCart(
        total = 100.0.toBigDecimal(),
        items = books.map { shoppingCartItem(it.id!!) }
    )

    private fun shoppingCartItem(id: UUID, quantity: Int = 1) =
        CreatePurchaseRequest.ShoppingCart.Item(id = id, quantity = quantity)
}