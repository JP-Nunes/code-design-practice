package category

import br.com.study.codedesignpractice.CodeDesignPracticeApplication
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post

@SpringBootTest(
    classes = [CodeDesignPracticeApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@Transactional
class RegisterCategoryIntegrationTest(@param:Autowired private val mockMvc: MockMvc) {

    companion object {
        private const val CATEGORIES_V1_PATH = "/v1/categories"
    }

    @Test
    fun `should be able to register a category`() {
        mockMvc.post(CATEGORIES_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            content = validCategoryRequest
        }.andExpect {
            status { isCreated() }
        }
    }

    @Test
    fun `should not be able to register a category that already exists`() {
        mockMvc.post(CATEGORIES_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            content = validCategoryRequest
        }.andExpect {
            status { isCreated() }
        }

        mockMvc.post(CATEGORIES_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            content = validCategoryRequest
        }.andExpect {
            status { isBadRequest() }
        }
    }

    val validCategoryRequest = """
        {
            "name": "sci-fi"
        }
    """.trimIndent()

    @Test
    fun `should be able to validate if the request have null parameters that should not be null`() {
        mockMvc.post(CATEGORIES_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            content = invalidCategoryRequestWithNullFields
        }.andExpect {
            status { isBadRequest() }
        }
    }

    val invalidCategoryRequestWithNullFields = """{}""".trimIndent()

    @Test
    fun `should be able to validate if the request have blank parameters that should not be blank`() {
        mockMvc.post(CATEGORIES_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            content = invalidCategoryRequestWithBlankFields
        }.andExpect {
            status { isBadRequest() }
        }
    }

    val invalidCategoryRequestWithBlankFields = """
        {
            "name": " "
        }
    """.trimIndent()

    @Test
    fun `should be able to validate if the request have empty parameters that should not be empty`() {
        mockMvc.post(CATEGORIES_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            content = invalidCategoryRequestWithEmptyFields
        }.andExpect {
            status { isBadRequest() }
        }
    }

    val invalidCategoryRequestWithEmptyFields = """
        {
            "name": ""
        }
    """.trimIndent()
}