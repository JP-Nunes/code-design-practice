package author

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
class RegisterAuthorIntegrationTest(@param:Autowired private val mockMvc: MockMvc) {

    companion object {
        private const val AUTHORS_V1_PATH = "/v1/authors"
    }

    @Test
    fun `should be able to register an author in the database and return response`() {
        mockMvc.post(AUTHORS_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            characterEncoding = "UTF-8"
            content = validAuthorRequest
        }.andExpect {
            status { isCreated() }
        }
    }

    @Test
    fun `should be able to validate if the email is already in use`() {
        mockMvc.post(AUTHORS_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            characterEncoding = "UTF-8"
            content = validAuthorRequest
        }.andExpect {
            status { isCreated() }
        }

        mockMvc.post(AUTHORS_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            characterEncoding = "UTF-8"
            content = validAuthorRequest
        }.andExpect {
            status { isBadRequest() }
        }
    }

    val validAuthorRequest = """
        {
            "name": "John Doe",
            "email": "john.doe@gmail.com",
            "description": "An author description"
        }
    """.trimIndent()

    @Test
    fun `should be able to validate null properties that shoud not be null`() {
        mockMvc.post(AUTHORS_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            characterEncoding = "UTF-8"
            content = invalidAuthorRequestWithNullFields
        }.andExpect {
            status { isBadRequest() }
        }
    }

    val invalidAuthorRequestWithNullFields = """{}""".trimIndent()

    @Test
    fun `should be able to validate empty properties that shoud not be empty`() {
        mockMvc.post(AUTHORS_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            characterEncoding = "UTF-8"
            content = invalidAuthorRequestWithEmptyFields
        }.andExpect {
            status { isBadRequest() }
        }
    }

    val invalidAuthorRequestWithEmptyFields = """
        {
            "name": "",
            "email": "",
            "description": ""
        }
    """.trimIndent()

    @Test
    fun `should be able to validate blank properties that shoud not be blank`() {
        mockMvc.post(AUTHORS_V1_PATH) {
            contentType = MediaType.APPLICATION_JSON
            characterEncoding = "UTF-8"
            content = invalidAuthorRequestWithBlankFields
        }.andExpect {
            status { isBadRequest() }
        }
    }

    val invalidAuthorRequestWithBlankFields = """
        {
            "name": " ",
            "email": " ",
            "description": " "
        }
    """.trimIndent()
}