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
import br.com.study.codedesignpractice.purchase.CreatePurchaseResponse
import br.com.study.codedesignpractice.voucher.VoucherRepository
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.json.JsonCompareMode
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import writeAsJson

@SpringBootTest(
    classes = [CodeDesignPracticeApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@Transactional
class RegisterPaymentValidationIntegrationTests(
    @param:Autowired private val mockMvc: MockMvc,
    @param:Autowired private val countryRepository: CountryRepository,
    @param:Autowired private val stateRepository: StateRepository,
    @param:Autowired private val categoryRepository: CategoryRepository,
    @param:Autowired private val authorRepository: AuthorRepository,
    @param:Autowired private val bookRepository: BookRepository,
    @param:Autowired private val voucherRepository: VoucherRepository
) {



    @Nested
    inner class TestRegisterPaymentEmailValidation {

        @Test
        fun `should not be able to register a payment with a blank email`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            val state = stateRepository.save(State(name = "Minas Gerais", country = country))
            val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
            val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
            val book = bookRepository.save(book(persistedCategory, persistedAuthor))
            val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHEREMOS"))

            val createPurchaseRequestWithBlank = createPaymentRequest(
                email = " ",
                countryId = country.id!!,
                stateId = state.id!!,
                shoppingCart = CreatePurchaseRequest.ShoppingCart(
                    total = 1.toBigDecimal(),
                    voucherCode = persistedVoucher.code,
                    items = listOf(CreatePurchaseRequest.ShoppingCart.Item(id = book.id!!, quantity = 1))
                )
            )

            mockMvc.post(PAYMENTS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = createPurchaseRequestWithBlank.writeAsJson()
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(
                        """{"invalidProperties":["email","email"],"errorMessages":["must be a well-formed email address","must not be blank"]}""",
                        JsonCompareMode.LENIENT
                    )
                }
            }.andDo { print() }
        }

        @Test
        fun `should not be able to register a payment with a null or whitespace email`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            val state = stateRepository.save(State(name = "Minas Gerais", country = country))
            val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
            val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
            val book = bookRepository.save(book(persistedCategory, persistedAuthor))
            val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHERZINHO"))

            val shoppingCart = CreatePurchaseRequest.ShoppingCart(
                total = 1.toBigDecimal(),
                voucherCode = persistedVoucher.code,
                items = listOf(CreatePurchaseRequest.ShoppingCart.Item(id = book.id!!, quantity = 1))
            )
            val createPaymentRequestWithEmptyEmail =
                createPaymentRequest(email = "", countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart)
            val createPaymentRequestWithNullEmail =
                createPaymentRequest(email = null, countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart)

            val invalidRequests = listOf(
                createPaymentRequestWithEmptyEmail,
                createPaymentRequestWithNullEmail
            )

            invalidRequests.forEach { request ->
                mockMvc.post(PAYMENTS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = request.writeAsJson()
                }.andExpect {
                    status { isBadRequest() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json(
                            """{"invalidProperties":["email"],"errorMessages":["must not be blank"]}""",
                            JsonCompareMode.STRICT
                        )
                    }
                }.andDo { print() }
            }
        }

        @Test
        fun `should validate if email is well formed`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            val state = stateRepository.save(State(name = "Rio de Janeiro", country = country))
            val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
            val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
            val book = bookRepository.save(book(persistedCategory, persistedAuthor))
            val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHER_TEST"))

            val createPurchaseRequestWithInvalidEmail = createPaymentRequest(
                email = "invalid-email",
                countryId = country.id!!,
                stateId = state.id!!,
                shoppingCart = CreatePurchaseRequest.ShoppingCart(
                    total = 1.toBigDecimal(),
                    voucherCode = persistedVoucher.code,
                    items = listOf(CreatePurchaseRequest.ShoppingCart.Item(id = book.id!!, quantity = 1))
                )
            )

            mockMvc.post(PAYMENTS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = createPurchaseRequestWithInvalidEmail.writeAsJson()
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json("""{"invalidProperties":["email"],"errorMessages":["must be a well-formed email address"]}""")
                }
            }.andDo { print() }
        }
    }

    @Nested
    inner class TestRegisterPaymentFirstNameValidation {

        @Test
        fun `should not be able to register a payment with a blank first name`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            val state = stateRepository.save(State(name = "Bahia", country = country))
            val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
            val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
            val book = bookRepository.save(book(persistedCategory, persistedAuthor))
            val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHER_TEST"))

            val shoppingCart = CreatePurchaseRequest.ShoppingCart(
                total = 1.toBigDecimal(),
                voucherCode = persistedVoucher.code,
                items = listOf(CreatePurchaseRequest.ShoppingCart.Item(id = book.id!!, quantity = 1))
            )
            val createPaymentRequestWithBlankFirstName = createPaymentRequest(firstName = " ", countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart)
            val createPaymentRequestWithEmptyFirstName = createPaymentRequest(firstName = "", countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart)
            val createPaymentRequestWithNullFirstName = createPaymentRequest(firstName = null, countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart)

            val invalidRequests = listOf(
                createPaymentRequestWithBlankFirstName,
                createPaymentRequestWithEmptyFirstName,
                createPaymentRequestWithNullFirstName
            )

            invalidRequests.forEach { request ->
                mockMvc.post(PAYMENTS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = request.writeAsJson()
                }.andExpect {
                    status { isBadRequest() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json("""{"invalidProperties":["firstName"],"errorMessages":["must not be blank"]}""")
                    }
                }.andDo { print() }
            }
        }
    }

    @Nested
    inner class TestRegisterPaymentLastNameValidation {

        @Test
        fun `should not be able to register a payment with a blank last name`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            val state = stateRepository.save(State(name = "Bahia", country = country))
            val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
            val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
            val book = bookRepository.save(book(persistedCategory, persistedAuthor))
            val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHER_TEST"))

            val shoppingCart = CreatePurchaseRequest.ShoppingCart(
                total = 1.toBigDecimal(),
                voucherCode = persistedVoucher.code,
                items = listOf(CreatePurchaseRequest.ShoppingCart.Item(id = book.id!!, quantity = 1))
            )
            val invalidRequests = listOf(
                createPaymentRequest(lastName = " ", countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart),
                createPaymentRequest(lastName = "", countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart),
                createPaymentRequest(lastName = null, countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart)
            )

            invalidRequests.forEach { request ->
                mockMvc.post(PAYMENTS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = request.writeAsJson()
                }.andExpect {
                    status { isBadRequest() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json("""{"invalidProperties":["lastName"],"errorMessages":["must not be blank"]}""")
                    }
                }.andDo { print() }
            }
        }
    }

    @Nested
    inner class TestRegisterPaymentDocumentValidation {

        @Test
        fun `should not be able to register a payment with a null document`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            val state = stateRepository.save(State(name = "Paraná", country = country))
            val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
            val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
            val book = bookRepository.save(book(persistedCategory, persistedAuthor))
            val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHER_TEST"))

            val request = createPaymentRequest(
                document = null,
                countryId = country.id!!,
                stateId = state.id!!,
                shoppingCart = CreatePurchaseRequest.ShoppingCart(
                    total = 1.toBigDecimal(),
                    voucherCode = persistedVoucher.code,
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
                    json(
                        """{"invalidProperties":["document"],"errorMessages":["must not be blank"]}""",
                        JsonCompareMode.STRICT
                    )
                }
            }.andDo { print() }
        }

        @Test
        fun `should not be able to register a payment with an empty or blank document`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            val state = stateRepository.save(State(name = "Minas Gerais", country = country))
            val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
            val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
            val book = bookRepository.save(book(persistedCategory, persistedAuthor))
            val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHER_DOC"))

            val shoppingCart = CreatePurchaseRequest.ShoppingCart(
                total = 1.toBigDecimal(),
                voucherCode = persistedVoucher.code,
                items = listOf(CreatePurchaseRequest.ShoppingCart.Item(id = book.id!!, quantity = 1))
            )
            val createPaymentRequestWithEmptyDocument = createPaymentRequest(
                document = "",
                countryId = country.id!!,
                stateId = state.id!!,
                shoppingCart = shoppingCart
            )
            val createPaymentRequestWithBlankDocument = createPaymentRequest(
                document = " ",
                countryId = country.id!!,
                stateId = state.id!!,
                shoppingCart = shoppingCart
            )

            val invalidRequests = listOf(
                createPaymentRequestWithEmptyDocument,
                createPaymentRequestWithBlankDocument
            )

            invalidRequests.forEach { request ->
                mockMvc.post(PAYMENTS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = request.writeAsJson()
                }.andExpect {
                    status { isBadRequest() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json(
                            """{"invalidProperties":["document","document"],"errorMessages":["Invalid document","must not be blank"]}""",
                            JsonCompareMode.LENIENT
                        )
                    }
                }.andDo { print() }
            }
        }

        @Test
        fun `should validate document format as CPF or CNPJ`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            val state = stateRepository.save(State(name = "Paraná", country = country))
            val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
            val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
            val book = bookRepository.save(book(persistedCategory, persistedAuthor))
            val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHER_DOCFMT"))

            val request = createPaymentRequest(
                document = "123",
                countryId = country.id!!,
                stateId = state.id!!,
                shoppingCart = CreatePurchaseRequest.ShoppingCart(
                    total = 1.toBigDecimal(),
                    voucherCode = persistedVoucher.code,
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
                    json(
                        """{"invalidProperties":["document"],"errorMessages":["Invalid document"]}""",
                        JsonCompareMode.LENIENT
                    )
                }
            }.andDo { print() }
        }
    }

    @Nested
    inner class TestRegisterPaymentAddressValidation {

        @Test
        fun `should not be able to register a payment with a blank address`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            val state = stateRepository.save(State(name = "Pará", country = country))
            val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
            val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
            val book = bookRepository.save(book(persistedCategory, persistedAuthor))
            val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHER_ADDR"))

            val shoppingCart = CreatePurchaseRequest.ShoppingCart(
                total = 1.toBigDecimal(),
                voucherCode = persistedVoucher.code,
                items = listOf(CreatePurchaseRequest.ShoppingCart.Item(id = book.id!!, quantity = 1))
            )
            val invalidRequests = listOf(
                createPaymentRequest(address = null,  countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart),
                createPaymentRequest(address = "", countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart),
                createPaymentRequest(address = " ",  countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart)
            )

            invalidRequests.forEach { request ->
                mockMvc.post(PAYMENTS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = request.writeAsJson()
                }.andExpect {
                    status { isBadRequest() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json("""{"invalidProperties":["address"],"errorMessages":["must not be blank"]}""")
                    }
                }.andDo { print() }
            }
        }
    }

    @Nested
    inner class TestRegisterPaymentComplementValidation {

        @Test
        fun `should not be able to register a payment with a blank complement`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            val state = stateRepository.save(State(name = "Santa Catarina", country = country))
            val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
            val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
            val book = bookRepository.save(book(persistedCategory, persistedAuthor))
            val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHER_COMP"))

            val shoppingCart = CreatePurchaseRequest.ShoppingCart(
                total = 1.toBigDecimal(),
                voucherCode = persistedVoucher.code,
                items = listOf(CreatePurchaseRequest.ShoppingCart.Item(id = book.id!!, quantity = 1))
            )
            val invalidRequests = listOf(
                createPaymentRequest(complement = null, countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart),
                createPaymentRequest(complement = "", countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart),
                createPaymentRequest(complement = " ", countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart)
            )

            invalidRequests.forEach { request ->
                mockMvc.post(PAYMENTS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = request.writeAsJson()
                }.andExpect {
                    status { isBadRequest() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json("""{"invalidProperties":["complement"],"errorMessages":["must not be blank"]}""")
                    }
                }.andDo { print() }
            }
        }
    }

    @Nested
    inner class TestRegisterPaymentCityValidation {

        @Test
        fun `should not be able to register a payment with a blank city`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            val state = stateRepository.save(State(name = "Ceará", country = country))
            val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
            val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
            val book = bookRepository.save(book(persistedCategory, persistedAuthor))
            val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHER_CITY"))

            val shoppingCart = CreatePurchaseRequest.ShoppingCart(
                total = 1.toBigDecimal(),
                voucherCode = persistedVoucher.code,
                items = listOf(CreatePurchaseRequest.ShoppingCart.Item(id = book.id!!, quantity = 1))
            )
            val invalidRequests = listOf(
                createPaymentRequest(city = null, countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart),
                createPaymentRequest(city = "", countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart),
                createPaymentRequest(city = " ", countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart)
            )

            invalidRequests.forEach { request ->
                mockMvc.post(PAYMENTS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = request.writeAsJson()
                }.andExpect {
                    status { isBadRequest() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json("""{"invalidProperties":["city"],"errorMessages":["must not be blank"]}""")
                    }
                }.andDo { print() }
            }
        }
    }

    @Nested
    inner class TestRegisterPaymentStateIdValidation {

        @Test
        fun `should be able to register a payment with null stateId if the country has no states registered in the system`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
            val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
            val book = bookRepository.save(book(persistedCategory, persistedAuthor))
            val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHER_STATE_NULL_NO_STATES"))

            val request = createPaymentRequest(
                countryId = country.id,
                stateId = null,
                shoppingCart = CreatePurchaseRequest.ShoppingCart(
                    total = 1.toBigDecimal(),
                    voucherCode = persistedVoucher.code,
                    items = listOf(CreatePurchaseRequest.ShoppingCart.Item(id = book.id!!, quantity = 1))
                )
            )

            val mockMvcResult = mockMvc.post(PAYMENTS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = request.writeAsJson()
            }
                .andExpect { status { isCreated() } }
                .andDo { print() }
                .andReturn()

            val actualResponse = mockMvcResult.response.contentAsString
            JSONAssert.assertEquals(expectedResponse(mockMvcResult), actualResponse, true)
        }

        fun expectedResponse(mockMvcResult: MvcResult): String {
            val mapper = jsonMapper { addModules(kotlinModule(), JavaTimeModule()) }
            val paymentResponse = mapper.readValue(mockMvcResult.response.contentAsString, CreatePurchaseResponse::class.java)

            val expected = """
                {
                  "id": "${paymentResponse.id}",
                  "buyerName": "John",
                  "buyerLastName": "Doe",
                  "phone": "+5511999999999",
                  "email": "test@example.com",
                  "address": "Av. Paulista, 1000",
                  "complement": "Ap 101",
                  "city": "São Paulo",
                  "country": {
                    "id": "${paymentResponse.country.id}",
                    "name": "Brazil"
                  },
                  "zipcode": "01310-000"
                }
            """.trimIndent()

            return expected
        }

        @Test
        fun `should not be able to register a payment with null stateId if the country has states registered in the system`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            stateRepository.save(State(name = "São Paulo", country = country))
            val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
            val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
            val book = bookRepository.save(book(persistedCategory, persistedAuthor))

            val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHER_STATE_NULL_WITH_STATES"))

            val request = createPaymentRequest(
                countryId = country.id,
                stateId = null,
                shoppingCart = CreatePurchaseRequest.ShoppingCart(
                    total = 1.toBigDecimal(),
                    voucherCode = persistedVoucher.code,
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
                    json("""{"invalidProperties":["stateId"],"errorMessages":["State does not belong to country"]}""")
                }
            }.andDo { print() }
        }

        @Test
        fun `should not be able to register a payment with stateId if the country has no states registered in the system`() {
            val persistedCountry = countryRepository.save(Country(name = "Brazil"))
            val otherPersistedCountry = countryRepository.save(Country(name = "Estados Unidos"))
            val persistedState = stateRepository.save(State(name = "California", country = otherPersistedCountry))
            val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
            val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
            val book = bookRepository.save(book(persistedCategory, persistedAuthor))

            val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHER_STATE_NO_STATES"))

            val request = createPaymentRequest(
                countryId = persistedCountry.id,
                stateId = persistedState.id,
                shoppingCart = CreatePurchaseRequest.ShoppingCart(
                    total = 1.toBigDecimal(),
                    voucherCode = persistedVoucher.code,
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
                    json("""{"invalidProperties":["stateId"],"errorMessages":["State does not belong to country"]}""")
                }
            }.andDo { print() }
        }

        @Test
        fun `should not be able to register a payment with a stateId that does not belong to the country`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            val anotherCountry = countryRepository.save(Country(name = "Canada"))
            val anotherCountryState = stateRepository.save(State(name = "Toronto", country = anotherCountry))
            val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
            val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
            val book = bookRepository.save(book(persistedCategory, persistedAuthor))

            val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHER_WRONG_STATE"))

            val request = createPaymentRequest(
                countryId = country.id,
                stateId = anotherCountryState.id,
                shoppingCart = CreatePurchaseRequest.ShoppingCart(
                    total = 1.toBigDecimal(),
                    voucherCode = persistedVoucher.code,
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
                    json("""{"invalidProperties":["stateId"],"errorMessages":["State does not belong to country"]}""")
                }
            }.andDo { print() }
        }
    }

    @Nested
    inner class TestRegisterPaymentCountryIdValidation {

        @Test
        fun `should not be able to register a payment with null countryId`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            val state = stateRepository.save(State(name = "São Paulo", country = country))
            val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
            val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
            val book = bookRepository.save(book(persistedCategory, persistedAuthor))

            val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHER_NULL_COUNTRY"))

            val request = createPaymentRequest(
                countryId = null,
                stateId = state.id!!,
                shoppingCart = CreatePurchaseRequest.ShoppingCart(
                    total = 1.toBigDecimal(),
                    voucherCode = persistedVoucher.code,
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
                    json("""{"invalidProperties":["countryId"],"errorMessages":["must not be null"]}""")
                }
            }.andDo { print() }
        }
    }

    @Nested
    inner class TestRegisterPaymentZipcodeValidation {

        @Test
        fun `should not be able to register a payment with a blank zipcode`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            val state = stateRepository.save(State(name = "Pernambuco", country = country))
            val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
            val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
            val book = bookRepository.save(book(persistedCategory, persistedAuthor))

            val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHER_ZIP"))

            val shoppingCart = CreatePurchaseRequest.ShoppingCart(
                total = 1.toBigDecimal(),
                voucherCode = persistedVoucher.code,
                items = listOf(CreatePurchaseRequest.ShoppingCart.Item(id = book.id!!, quantity = 1))
            )
            val invalidRequests = listOf(
                createPaymentRequest(zipcode = null, countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart),
                createPaymentRequest(zipcode = "", countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart),
                createPaymentRequest(zipcode = " ", countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart)
            )

            invalidRequests.forEach { request ->
                mockMvc.post(PAYMENTS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = request.writeAsJson()
                }.andExpect {
                    status { isBadRequest() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json("""{"invalidProperties":["zipcode"],"errorMessages":["must not be blank"]}""")
                    }
                }.andDo { print() }
            }
        }
    }

    @Nested
    inner class TestRegisterPaymentPhoneValidation {

        @Test
        fun `should not be able to register a payment with a blank phone`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            val state = stateRepository.save(State(name = "Goiás", country = country))
            val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
            val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
            val book = bookRepository.save(book(persistedCategory, persistedAuthor))

            val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHER_PHONE"))

            val shoppingCart = CreatePurchaseRequest.ShoppingCart(
                total = 1.toBigDecimal(),
                voucherCode = persistedVoucher.code,
                items = listOf(CreatePurchaseRequest.ShoppingCart.Item(id = book.id!!, quantity = 1))
            )
            val invalidRequests = listOf(
                createPaymentRequest(phone = null, countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart),
                createPaymentRequest(phone = "", countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart),
                createPaymentRequest(phone = " ", countryId = country.id!!, stateId = state.id!!, shoppingCart = shoppingCart)
            )

            invalidRequests.forEach { request ->
                mockMvc.post(PAYMENTS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = request.writeAsJson()
                }.andExpect {
                    status { isBadRequest() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json("""{"invalidProperties":["phone"],"errorMessages":["must not be blank"]}""")
                    }
                }.andDo { print() }
            }
        }
    }
}