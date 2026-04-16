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
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional
import toClass
import writeAsJson
import java.util.UUID

@SpringBootTest(
    classes = [CodeDesignPracticeApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@Transactional
class FindPurchaseIntegrationTests(
    @param:Autowired private val mockMvc: MockMvc,
    @param:Autowired private val countryRepository: CountryRepository,
    @param:Autowired private val stateRepository: StateRepository,
    @param:Autowired private val categoryRepository: CategoryRepository,
    @param:Autowired private val authorRepository: AuthorRepository,
    @param:Autowired private val bookRepository: BookRepository,
    @param:Autowired private val voucherRepository: VoucherRepository
) {

    @Test
    fun `should return purchase details by id including coupon fields`() {
        val country = countryRepository.save(Country(name = "Brazil"))
        val state = stateRepository.save(State(name = "São Paulo", country = country))
        val persistedCategory = categoryRepository.save(Category(name = "Non Fiction"))
        val persistedAuthor = authorRepository.save(Author("Mark Richards", "mark.richards@email.com", "A sample author"))
        val book = bookRepository.save(book(persistedCategory, persistedAuthor))
        val persistedVoucher = voucherRepository.save(voucher(code = "VOUCHER10"))
        val validPaymentRequest = createPaymentRequest(
            countryId = country.id!!,
            stateId = state.id!!,
            voucherCode = persistedVoucher.code,
            shoppingCart = CreatePurchaseRequest.ShoppingCart(
                total = 1.toBigDecimal(),
                items = listOf(CreatePurchaseRequest.ShoppingCart.Item(id = book.id!!, quantity = 1))
            )
        )

        val createResult = mockMvc.post(PAYMENTS_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            content = validPaymentRequest.writeAsJson()
        }
            .andExpect { status { isCreated() } }
            .andReturn()

        val paymentResponse = createResult.response.contentAsString.toClass<CreatePurchaseResponse>()

        val findResult = mockMvc.get("$PAYMENTS_V1_PATH/${paymentResponse.id}")
            .andExpect { status { isOk() } }
            .andReturn()

        val actual = findResult.response.contentAsString
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
              "zipcode": "01310-000",
              "total": 20.00,
              "totalWithDiscount": 18.00,
              "hasCoupon": true,
              "totalWithCouponApplied": 18.00
            }
        """.trimIndent()

        JSONAssert.assertEquals(expected, actual, true)
    }

    @Test
    fun `should return 404 when id does not exist`() {
        mockMvc.get("$PAYMENTS_V1_PATH/${UUID.randomUUID()}")
            .andExpect { status { isNotFound() } }
    }
}
