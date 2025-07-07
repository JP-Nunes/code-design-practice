import br.com.study.codedesignpractice.CodeDesignPracticeApplication
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.json.JsonCompareMode
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.post
import org.springframework.transaction.annotation.Transactional

@SpringBootTest(
    classes = [CodeDesignPracticeApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
class SaveAuthorIntegrationTest(@Autowired private val mockMvc: MockMvc) {

    companion object {
        private const val AUTHORS_ENDPOINT = "/authors"
    }

    @Test
    fun `should be able to register an author in the database and return response`() {
        mockMvc.post(AUTHORS_ENDPOINT) {
            contentType = MediaType.APPLICATION_JSON
            content = validAuthorRequest
        }.andExpect {
            status { isCreated() }
        }
    }

    @Test
    fun `should be able to validate if the email is already in use`() {
        mockMvc.post(AUTHORS_ENDPOINT) {
            contentType = MediaType.APPLICATION_JSON
            content = validAuthorRequest
        }

        mockMvc.post(AUTHORS_ENDPOINT) {
            contentType = MediaType.APPLICATION_JSON
            content = validAuthorRequest
        }.andExpect {
            status { isBadRequest() }
        }.andDo {
            handle { println(it.response.contentAsString) }
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
        mockMvc.post(AUTHORS_ENDPOINT) {
            contentType = MediaType.APPLICATION_JSON
            content = invalidAuthorRequestWithNullFields
        }.andExpect {
            status { isBadRequest() }
        }
    }

    val invalidAuthorRequestWithNullFields = """{}""".trimIndent()

    @Test
    fun `should be able to validate empty properties that shoud not be empty`() {
        mockMvc.post(AUTHORS_ENDPOINT) {
            contentType = MediaType.APPLICATION_JSON
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
        mockMvc.post(AUTHORS_ENDPOINT) {
            contentType = MediaType.APPLICATION_JSON
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