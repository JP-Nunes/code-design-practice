@file:Suppress("FunctionName")

package author

import br.com.study.codedesignpractice.CodeDesignPracticeApplication
import br.com.study.codedesignpractice.author.AuthorRequest
import br.com.study.codedesignpractice.author.AuthorResponse
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.http.HttpStatus

@SpringBootTest(
    classes = [CodeDesignPracticeApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class SaveAuthorIntegrationTest(@Autowired private val restTemplate: TestRestTemplate) {

    @Test fun `should be able to register an author in the database and return response`() {
        val authorRequest = AuthorRequest(
            name = "John Doe",
            email = "john.doe@gmail.com",
            description = "An author description"
        )

        val response = restTemplate.postForEntity("/authors", authorRequest, AuthorResponse::class.java)
        Assertions.assertThat(response.statusCode).isEqualTo(HttpStatus.CREATED)
    }
}