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
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.json.JsonCompareMode
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import writeAsJson

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
    @param:Autowired private val bookRepository: BookRepository
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

            val createPaymentRequestWithBlank = createPaymentRequest(email = " ", countryId = country.id!!, stateId = state.id!!, shoppingCartItemId = book.id)

            mockMvc.post(PAYMENTS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = createPaymentRequestWithBlank.writeAsJson()
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

            val createPaymentRequestWithEmptyEmail =
                createPaymentRequest(email = "", countryId = country.id!!, stateId = state.id!!, shoppingCartItemId = book.id)
            val createPaymentRequestWithNullEmail =
                createPaymentRequest(email = null, countryId = country.id!!, stateId = state.id!!, shoppingCartItemId = book.id)

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

            val createPaymentRequestWithInvalidEmail =
                createPaymentRequest(email = "invalid-email", countryId = country.id!!, stateId = state.id!!, shoppingCartItemId = book.id)

            mockMvc.post(PAYMENTS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = createPaymentRequestWithInvalidEmail.writeAsJson()
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json("""{"invalidProperties":["email"],"errorMessages":["must be a well-formed email address"]}""")
                }
            }.andDo { print() }
        }
    }
}