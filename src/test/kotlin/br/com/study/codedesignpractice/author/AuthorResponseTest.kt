package br.com.study.codedesignpractice.author

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.*

class AuthorResponseTest {

    @Test
    fun `should be able to create AuthorResponse from Author model`() {
        val author = Author(
            name = "Jane Doe",
            email = "jane.doe@gmail.com",
            description = "An example author",
            id = UUID.randomUUID()
        )

        val expected = AuthorResponse(author.id)
        val actual = AuthorResponse.fromEntity(author)

        assertThat(expected).isEqualTo(actual)
    }
}