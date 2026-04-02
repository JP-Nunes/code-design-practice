package payment

import br.com.study.codedesignpractice.CodeDesignPracticeApplication
import br.com.study.codedesignpractice.author.Author
import br.com.study.codedesignpractice.author.AuthorRepository
import br.com.study.codedesignpractice.book.repository.BookRepository
import br.com.study.codedesignpractice.category.Category
import br.com.study.codedesignpractice.category.CategoryRepository
import br.com.study.codedesignpractice.location.country.Country
import br.com.study.codedesignpractice.location.country.CountryRepository
import br.com.study.codedesignpractice.location.state.State
import br.com.study.codedesignpractice.location.state.StateRepository
import br.com.study.codedesignpractice.purchase.CreatePurchaseRequest
import br.com.study.codedesignpractice.voucher.VoucherRepository
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import writeAsJson
import java.util.UUID

@SpringBootTest(
    classes = [CodeDesignPracticeApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@Transactional
class RegisterPaymentShoppingCartValidationIntegrationTests(
    @param:Autowired private val mockMvc: MockMvc,
    @param:Autowired private val countryRepository: CountryRepository,
    @param:Autowired private val stateRepository: StateRepository,
    @param:Autowired private val categoryRepository: CategoryRepository,
    @param:Autowired private val authorRepository: AuthorRepository,
    @param:Autowired private val bookRepository: BookRepository,
    @param:Autowired private val voucherRepository: VoucherRepository
) {



    @Test
    fun `should not be able to register a payment with a null shopping cart`() {
        val country = countryRepository.save(Country(name = "Brazil"))
        val state = stateRepository.save(State(name = "Goiás", country = country))

        val request = createPaymentRequest(
            countryId = country.id!!,
            stateId = state.id!!,
            shoppingCart = null
        )

        mockMvc.post(PAYMENTS_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            content = request.writeAsJson()
        }.andExpect {
            status { isBadRequest() }
            content {
                contentType(MediaType.APPLICATION_JSON)
                json("""{"invalidProperties":["shoppingCart"],"errorMessages":["must not be null"]}""")
            }
        }.andDo { print() }
    }

    @Nested
    inner class TestRegisterPaymentShoppingCartTotalValidation {

        @Test
        fun `should not be able to register a payment with a shopping cart with a null total`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            val state = stateRepository.save(State(name = "Goiás", country = country))
            val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
            val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
            val book = bookRepository.save(book(persistedCategory, persistedAuthor))
            val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHER10"))

            val request = createPaymentRequest(
                countryId = country.id!!,
                stateId = state.id!!,
                voucherCode = persistedVoucher.code,
                shoppingCart = CreatePurchaseRequest.ShoppingCart(
                    total = null,
                    items = listOf(CreatePurchaseRequest.ShoppingCart.Item(id = book.id!!, quantity = 1))
                )
            )

            mockMvc.post(PAYMENTS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = request.writeAsJson()
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json("""{"invalidProperties":["shoppingCart.total"],"errorMessages":["must not be null"]}""")
                }
            }.andDo { print() }
        }

        @Test
        fun `should not be able to register a payment with a shopping cart with a total smaller than 0_01`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            val state = stateRepository.save(State(name = "Goiás", country = country))
            val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
            val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
            val book = bookRepository.save(book(persistedCategory, persistedAuthor))

            val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHER18"))

            val request = createPaymentRequest(
                countryId = country.id!!,
                stateId = state.id!!,
                voucherCode = persistedVoucher.code,
                shoppingCart = CreatePurchaseRequest.ShoppingCart(
                    total = 0.0.toBigDecimal(),
                    items = listOf(CreatePurchaseRequest.ShoppingCart.Item(id = book.id!!, quantity = 1))
                )
            )

            mockMvc.post(PAYMENTS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = request.writeAsJson()
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json("""{"invalidProperties":["shoppingCart.total"],"errorMessages":["must be greater than or equal to 0.01"]}""")
                }
            }.andDo { print() }
        }
    }

    @Nested
    inner class TestRegisterPaymentShoppingCartItemsValidation {

        @Test
        fun `should not be able to register a payment with a shopping cart with an empty list of items`() {
            val country = countryRepository.save(Country(name = "India"))
            val state = stateRepository.save(State(name = "Delhi", country = country))
            val persistedVoucher = voucherRepository.save(voucher(code = "PROMO15"))

            val request = createPaymentRequest(
                countryId = country.id!!,
                stateId = state.id!!,
                voucherCode = persistedVoucher.code,
                shoppingCart = CreatePurchaseRequest.ShoppingCart(
                    total = 0.10.toBigDecimal(),
                    items = emptyList()
                )
            )

            mockMvc.post(PAYMENTS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = request.writeAsJson()
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json("""{"invalidProperties":["shoppingCart.items"],"errorMessages":["must not be empty"]}""")
                }
            }.andDo { print() }
        }

        @Test
        fun `should not be able to register a payment with a shopping cart with an item that has a null id`() {
            val country = countryRepository.save(Country(name = "India"))
            val state = stateRepository.save(State(name = "Delhi", country = country))

            val persistedVoucher = voucherRepository.save(voucher(code = "SUPERPROMO10"))

            val request = createPaymentRequest(
                countryId = country.id!!,
                stateId = state.id!!,
                voucherCode = persistedVoucher.code,
                shoppingCart = CreatePurchaseRequest.ShoppingCart(
                    total = 0.10.toBigDecimal(),
                    items = listOf(CreatePurchaseRequest.ShoppingCart.Item(id = null, quantity = 1))
                )
            )

            mockMvc.post(PAYMENTS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = request.writeAsJson()
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json("""{"invalidProperties":["shoppingCart.items[0].id"],"errorMessages":["must not be null"]}""")
                }
            }.andDo { print() }
        }

        @Test
        fun `should not be able to register a payment with a shopping cart with an item that does not exist in the database`() {
            val country = countryRepository.save(Country(name = "India"))
            val state = stateRepository.save(State(name = "Delhi", country = country))

            val persistedVoucher = voucherRepository.save(voucher(code = "SUPERPROMO50"))

            val request = createPaymentRequest(
                countryId = country.id!!,
                stateId = state.id!!,
                voucherCode = persistedVoucher.code,
                shoppingCart = CreatePurchaseRequest.ShoppingCart(
                    total = 0.10.toBigDecimal(),
                    voucherCode = persistedVoucher.code,
                    items = listOf(CreatePurchaseRequest.ShoppingCart.Item(id = UUID.randomUUID(), quantity = 1))
                )
            )

            mockMvc.post(PAYMENTS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = request.writeAsJson()
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json("""{"invalidProperties":["shoppingCart.items[0].id"],"errorMessages":["Value must exist in the database"]}""".trimMargin())
                }
            }.andDo { print() }
        }

        @Test
        fun `should not be able to register a payment with a shopping cart with an item that has a null quantity`() {
            val country = countryRepository.save(Country(name = "India"))
            val state = stateRepository.save(State(name = "Delhi", country = country))
            val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
            val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
            val book = bookRepository.save(book(persistedCategory, persistedAuthor))

            val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHERINFLUENCERX"))

            val request = createPaymentRequest(
                countryId = country.id,
                stateId = state.id,
                shoppingCart = CreatePurchaseRequest.ShoppingCart(
                    total = 0.10.toBigDecimal(),
                    voucherCode = persistedVoucher.code,
                    items = listOf(CreatePurchaseRequest.ShoppingCart.Item(id = book.id, quantity = null))
                )
            )

            mockMvc.post(PAYMENTS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = request.writeAsJson()
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json("""{"invalidProperties":["shoppingCart.items[0].quantity"],"errorMessages":["must not be null"]}""".trimMargin())
                }
            }.andDo { print() }
        }

        @Test
        fun `should not be able to register a payment with a shopping cart with an item that a quantity of at least 1`() {
            val country = countryRepository.save(Country(name = "India"))
            val state = stateRepository.save(State(name = "Delhi", country = country))
            val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
            val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
            val book = bookRepository.save(book(persistedCategory, persistedAuthor))

            val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHER1000"))

            val request = createPaymentRequest(
                countryId = country.id,
                stateId = state.id,
                shoppingCart = CreatePurchaseRequest.ShoppingCart(
                    total = 0.10.toBigDecimal(),
                    voucherCode = persistedVoucher.code,
                    items = listOf(CreatePurchaseRequest.ShoppingCart.Item(id = book.id, quantity = 0))
                )
            )

            mockMvc.post(PAYMENTS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = request.writeAsJson()
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json("""{"invalidProperties":["shoppingCart.items[0].quantity"],"errorMessages":["must be greater than or equal to 1"]}""".trimMargin())
                }
            }.andDo { print() }
        }
    }
}