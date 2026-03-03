package voucher

import br.com.study.codedesignpractice.CodeDesignPracticeApplication
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import java.time.LocalDate

@SpringBootTest(
    classes = [CodeDesignPracticeApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@Transactional
class RegisterVoucherIntegrationTest() {

    @Autowired private lateinit var mockMvc: MockMvc

    companion object {
        private const val VOUCHERS_V1_PATH = "/v1/vouchers"
    }

    @Test
    fun `should be able to register a voucher in the database and return response`() {
        val tomorrow = LocalDate.now().plusDays(1)
        val validVoucherRequest = """
            {
                "code": "SUMMER-10",
                "discount": 10.0,
                "expirationDate": "$tomorrow"
            }
        """.trimIndent()

        mockMvc.post(VOUCHERS_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            characterEncoding = "UTF-8"
            content = validVoucherRequest
        }.andExpect {
            status { isCreated() }
        }
    }

    @Test
    fun `should be able to validate null properties that should not be null`() {
        val invalidVoucherRequestWithNullFields = """{}""".trimIndent()

        mockMvc.post(VOUCHERS_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            characterEncoding = "UTF-8"
            content = invalidVoucherRequestWithNullFields
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `should be able to validate empty properties that should not be empty`() {
        val invalidVoucherRequestWithEmptyFields = """
            {
                "code": "",
                "discount": "",
                "expirationDate": ""
            }
        """.trimIndent()

        mockMvc.post(VOUCHERS_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            characterEncoding = "UTF-8"
            content = invalidVoucherRequestWithEmptyFields
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `should be able to validate blank properties that should not be blank`() {
        val invalidVoucherRequestWithBlankFields = """
            {
                "code": " ",
                "discount": " ",
                "expirationDate": " "
            }
        """.trimIndent()

        mockMvc.post(VOUCHERS_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            characterEncoding = "UTF-8"
            content = invalidVoucherRequestWithBlankFields
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `should be able to validate a negative discount`() {
        val tomorrow = LocalDate.now().plusDays(1)
        val invalidNegativeDiscountRequest = """
            {
                "code": "SUMMER-10",
                "discount": -5,
                "expirationDate": "$tomorrow"
            }
        """.trimIndent()

        mockMvc.post(VOUCHERS_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            characterEncoding = "UTF-8"
            content = invalidNegativeDiscountRequest
        }.andExpect {
            status { isBadRequest() }
        }
    }

    @Test
    fun `should be able to validate when expiration date is in the past`() {
        val yesterday = LocalDate.now().minusDays(1)
        val invalidPastExpirationRequest = """
            {
                "code": "SUMMER-10",
                "discount": 10,
                "expirationDate": "$yesterday"
            }
        """.trimIndent()

        mockMvc.post(VOUCHERS_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            characterEncoding = "UTF-8"
            content = invalidPastExpirationRequest
        }.andExpect {
            status { isBadRequest() }
        }
    }
}
