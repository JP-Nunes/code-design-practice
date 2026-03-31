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
import br.com.study.codedesignpractice.voucher.Voucher
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import toClass
import writeAsJson
import java.time.LocalDate

@SpringBootTest(
    classes = [CodeDesignPracticeApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@Transactional
class RegisterPaymentIntegrationTest(
    @param:Autowired private val mockMvc: MockMvc,
    @param:Autowired private val countryRepository: CountryRepository,
    @param:Autowired private val stateRepository: StateRepository,
    @param:Autowired private val categoryRepository: CategoryRepository,
    @param:Autowired private val authorRepository: AuthorRepository,
    @param:Autowired private val bookRepository: BookRepository,
    @param:Autowired private val voucherRepository: br.com.study.codedesignpractice.voucher.VoucherRepository
) {

    @Test
    fun `should be able to register a payment with cpf`() {
        val country = countryRepository.save(Country(name = "Brazil"))
        val state = stateRepository.save(State(name = "São Paulo", country = country))
        val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
        val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
        val book = bookRepository.save(book(persistedCategory, persistedAuthor))

        val cpf = "67674204090"
        voucherRepository.save(
            Voucher(
                code = "VOUCHER123",
                discount = 10.toBigDecimal(),
                expirationDate = LocalDate.now().plusDays(10)
            )
        )
        val validPaymentRequest = createPaymentRequest(
            countryId = country.id!!,
            stateId = state.id!!,
            document = cpf,
            shoppingCart = CreatePurchaseRequest.ShoppingCart(
                total = 1.toBigDecimal(),
                voucherCode = "VOUCHER123",
                items = listOf(CreatePurchaseRequest.ShoppingCart.Item(id = book.id!!, quantity = 1))
            )
        )

        val mockMvcResult = mockMvc.post(PAYMENTS_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            content = validPaymentRequest.writeAsJson()
        }
            .andExpect { status { isCreated() } }
            .andDo { print() }
            .andReturn()

        val actualResponse = mockMvcResult.response.contentAsString
        JSONAssert.assertEquals(expectedResponse(mockMvcResult), actualResponse, true)
    }

    @Test
    fun `should be able to register a payment with cnpj`() {
        val country = countryRepository.save(Country(name = "Brazil"))
        val state = stateRepository.save(State(name = "São Paulo", country = country))
        val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
        val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
        val book = bookRepository.save(book(persistedCategory, persistedAuthor))

        val cnpj = "29232727000186"
        voucherRepository.save(
            Voucher(
                code = "VOUCHER10",
                discount = 10.toBigDecimal(),
                expirationDate = LocalDate.now().plusDays(10)
            )
        )
        val validPaymentRequest = createPaymentRequest(
            countryId = country.id!!,
            stateId = state.id!!,
            document = cnpj,
            shoppingCart = CreatePurchaseRequest.ShoppingCart(
                total = 1.toBigDecimal(),
                voucherCode = "VOUCHER10",
                items = listOf(CreatePurchaseRequest.ShoppingCart.Item(id = book.id!!, quantity = 1))
            )
        )

        val mockMvcResult = mockMvc.post(PAYMENTS_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            content = validPaymentRequest.writeAsJson()
        }
            .andExpect { status { isCreated() } }
            .andDo { print() }
            .andReturn()

        val actualResponse = mockMvcResult.response.contentAsString
        JSONAssert.assertEquals(expectedResponse(mockMvcResult), actualResponse, true)
    }

    fun expectedResponse(mockMvcResult: MvcResult): String {
        val paymentResponse = mockMvcResult.response.contentAsString.toClass<CreatePurchaseResponse>()

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
              "state": {
                "id": "${paymentResponse.state?.id}",
                "name": "São Paulo",
                "country": {
                  "id": "${paymentResponse.country.id}",
                  "name": "Brazil"
                }
              },
              "zipcode": "01310-000"
            }
        """.trimIndent()

        return expected
    }
}
