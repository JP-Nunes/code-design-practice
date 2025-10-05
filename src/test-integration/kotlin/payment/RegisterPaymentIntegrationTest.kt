package payment

import br.com.study.codedesignpractice.CodeDesignPracticeApplication
import br.com.study.codedesignpractice.location.country.Country
import br.com.study.codedesignpractice.location.country.CountryRepository
import br.com.study.codedesignpractice.location.state.State
import br.com.study.codedesignpractice.location.state.StateRepository
import br.com.study.codedesignpractice.payment.CreatePaymentRequest
import br.com.study.codedesignpractice.payment.CreatePaymentResponse
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
import java.util.*

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
) {

    companion object {
        const val PAYMENTS_V1_PATH = "/v1/payments"
    }

    @Nested
    inner class TestRegisterPaymentSuccess {

        @Test
        fun `should be able to register a payment with cpf`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            val state = stateRepository.save(State(name = "São Paulo", country = country))

            val cpf = "67674204090"
            val validPaymentRequest = createPaymentRequest(
                countryId = country.id!!,
                stateId = state.id!!,
                document = cpf
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

            val cnpj = "29232727000186"
            val validPaymentRequest = createPaymentRequest(
                countryId = country.id!!,
                stateId = state.id!!,
                document = cnpj
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
            val mapper = jsonMapper { addModules(kotlinModule(), JavaTimeModule()) }
            val paymentResponse = mapper.readValue(mockMvcResult.response.contentAsString, CreatePaymentResponse::class.java)

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
                    "id": "${paymentResponse.state.id}",
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

    @Nested
    inner class TestRegisterPaymentEmailValidation {

        @Test
        fun `should not be able to register a payment with a blank email`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            val state = stateRepository.save(State(name = "Minas Gerais", country = country))

            val createPaymentRequestWithBlank = createPaymentRequest(email = " ", countryId = country.id!!, stateId = state.id!!)

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

            val createPaymentRequestWithEmptyEmail =
                createPaymentRequest(email = "", countryId = country.id!!, stateId = state.id!!)
            val createPaymentRequestWithNullEmail =
                createPaymentRequest(email = null, countryId = country.id!!, stateId = state.id!!)

            val invalidRequests = listOf(
                createPaymentRequestWithEmptyEmail,
                createPaymentRequestWithNullEmail
            )

            invalidRequests.forEach { req ->
                mockMvc.post(PAYMENTS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = req.writeAsJson()
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

            val createPaymentRequestWithInvalidEmail =
                createPaymentRequest(email = "invalid-email", countryId = country.id!!, stateId = state.id!!)

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

    @Nested
    inner class TestRegisterPaymentFirstNameValidation {

        @Test
        fun `should not be able to register a payment with a blank first name`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            val state = stateRepository.save(State(name = "Bahia", country = country))

            val createPaymentRequestWithBlankFirstName = createPaymentRequest(firstName = " ", countryId = country.id!!, stateId = state.id!!)
            val createPaymentRequestWithEmptyFirstName = createPaymentRequest(firstName = "", countryId = country.id!!, stateId = state.id!!)
            val createPaymentRequestWithNullFirstName = createPaymentRequest(firstName = null, countryId = country.id!!, stateId = state.id!!)

            val invalidRequests = listOf(
                createPaymentRequestWithBlankFirstName,
                createPaymentRequestWithEmptyFirstName,
                createPaymentRequestWithNullFirstName
            )

            invalidRequests.forEach { req ->
                mockMvc.post(PAYMENTS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = req.writeAsJson()
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

            val invalidRequests = listOf(
                createPaymentRequest(lastName = " ", countryId = country.id!!, stateId = state.id!!),
                createPaymentRequest(lastName = "", countryId = country.id!!, stateId = state.id!!),
                createPaymentRequest(lastName = null, countryId = country.id!!, stateId = state.id!!)
            )

            invalidRequests.forEach { req ->
                mockMvc.post(PAYMENTS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = req.writeAsJson()
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

            val req = createPaymentRequest(document = null, countryId = country.id!!, stateId = state.id!!)

            mockMvc.post(PAYMENTS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = req.writeAsJson()
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

            val createPaymentRequestWithEmptyDocument =
                createPaymentRequest(document = "", countryId = country.id!!, stateId = state.id!!)
            val createPaymentRequestWithBlankDocument =
                createPaymentRequest(document = " ", countryId = country.id!!, stateId = state.id!!)

            val invalidRequests = listOf(
                createPaymentRequestWithEmptyDocument,
                createPaymentRequestWithBlankDocument
            )

            invalidRequests.forEach { req ->
                mockMvc.post(PAYMENTS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = req.writeAsJson()
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

            val req = createPaymentRequest(document = "123", countryId = country.id!!, stateId = state.id!!)

            mockMvc.post(PAYMENTS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = req.writeAsJson()
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

            val invalidRequests = listOf(
                createPaymentRequest(address = null,  countryId = country.id!!, stateId = state.id!!),
                createPaymentRequest(address = "", countryId = country.id!!, stateId = state.id!!),
                createPaymentRequest(address = " ",  countryId = country.id!!, stateId = state.id!!)
            )

            invalidRequests.forEach { req ->
                mockMvc.post(PAYMENTS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = req.writeAsJson()
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

            val invalidRequests = listOf(
                createPaymentRequest(complement = null, countryId = country.id!!, stateId = state.id!!),
                createPaymentRequest(complement = "", countryId = country.id!!, stateId = state.id!!),
                createPaymentRequest(complement = " ", countryId = country.id!!, stateId = state.id!!)
            )

            invalidRequests.forEach { req ->
                mockMvc.post(PAYMENTS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = req.writeAsJson()
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

            val invalidRequests = listOf(
                createPaymentRequest(city = null, countryId = country.id!!, stateId = state.id!!),
                createPaymentRequest(city = "", countryId = country.id!!, stateId = state.id!!),
                createPaymentRequest(city = " ", countryId = country.id!!, stateId = state.id!!)
            )

            invalidRequests.forEach { req ->
                mockMvc.post(PAYMENTS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = req.writeAsJson()
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
    inner class TestRegisterPaymentCountryIdValidation {

        @Test
        fun `should not be able to register a payment with null countryId`() {
            val country = countryRepository.save(Country(name = "Brazil"))
            val state = stateRepository.save(State(name = "São Paulo", country = country))

            val req = createPaymentRequest(countryId = null, stateId = state.id!!)

            mockMvc.post(PAYMENTS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = req.writeAsJson()
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

            val invalidRequests = listOf(
                createPaymentRequest(zipcode = null, countryId = country.id!!, stateId = state.id!!),
                createPaymentRequest(zipcode = "", countryId = country.id!!, stateId = state.id!!),
                createPaymentRequest(zipcode = " ", countryId = country.id!!, stateId = state.id!!)
            )

            invalidRequests.forEach { req ->
                mockMvc.post(PAYMENTS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = req.writeAsJson()
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

            val invalidRequests = listOf(
                createPaymentRequest(phone = null, countryId = country.id!!, stateId = state.id!!),
                createPaymentRequest(phone = "", countryId = country.id!!, stateId = state.id!!),
                createPaymentRequest(phone = " ", countryId = country.id!!, stateId = state.id!!)
            )

            invalidRequests.forEach { req ->
                mockMvc.post(PAYMENTS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = req.writeAsJson()
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

    fun createPaymentRequest(
        email: String? = "test@example.com",
        firstName: String? = "John",
        lastName: String? = "Doe",
        document: String? = "793.222.040-87",
        address: String? = "Av. Paulista, 1000",
        complement: String? = "Ap 101",
        city: String? = "São Paulo",
        countryId: UUID?,
        stateId: UUID?,
        zipcode: String? = "01310-000",
        phone: String? = "+5511999999999"
    ) = CreatePaymentRequest(
        email = email,
        firstName = firstName,
        lastName = lastName,
        document = document,
        address = address,
        complement = complement,
        city = city,
        countryId = countryId,
        stateId = stateId,
        zipcode = zipcode,
        phone = phone
    )
}
