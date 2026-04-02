package br.com.study.codedesignpractice.purchase

import br.com.study.codedesignpractice.author.Author
import br.com.study.codedesignpractice.book.repository.Book
import br.com.study.codedesignpractice.category.Category
import br.com.study.codedesignpractice.location.country.Country
import br.com.study.codedesignpractice.location.state.State
import br.com.study.codedesignpractice.voucher.Voucher
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import java.time.LocalDate

@DataJpaTest
class PaymentRepositoryTest @Autowired constructor(
    val purchaseRepository: PurchaseRepository,
    val entityManager: TestEntityManager
) {

    @Test
    fun `should be able to register a new payment`() {
        val country = Country(name = "Brazil")
        val state = State(name = "São Paulo", country = country)

        entityManager.persist(country)
        entityManager.persist(state)

        val category = Category(name = "fiction")
        val author = Author(
            name = "John Doe",
            email = "john.doe@hotmail.com",
            description = "A sample author"
        )

        val persistedCategory = entityManager.persist(category)
        val persistedAuthor = entityManager.persist(author)

        val voucher = Voucher(code = "VOUCHER15", discount = 10.toBigDecimal(), expirationDate = LocalDate.now().plusDays(10))
        val persistedVoucher = entityManager.persist(voucher)

        val book = Book(
            title = "Book Title",
            summary = "Book summary",
            tableOfContents = "Markdown table of contents",
            price = 250.toBigDecimal(),
            numberOfPages = 150,
            isbn = "123-456-789",
            publishDate = LocalDate.now().plusDays(10),
            category = persistedCategory!!,
            author = persistedAuthor!!
        )

        val persistedBook = entityManager.persist(book)

        val purchase = Purchase(
            email = "user@buyer.com",
            firstName = "Zacarias",
            lastName = "Stoth",
            document = "08625390056",
            address = "Rua das ruas, 1234",
            complement = "Apartemento 200",
            city = "São Paulo",
            country = country,
            state = state,
            phone = "+5511988714077",
            zipcode = "01310-000",
            voucher = persistedVoucher,
            shoppingCart = Purchase.ShoppingCart(
                total = 100.0.toBigDecimal(),
                items = listOf(
                    Purchase.ShoppingCart.Item(book = persistedBook, quantity = 2)
                )
            )
        )

        entityManager.persist(purchase)
        entityManager.flush()

        val persistedPayment = purchaseRepository.findById(purchase.id!!).get()

        assertThat(persistedPayment).isEqualTo(purchase)
    }
 }