package br.com.study.codedesignpractice.author

import io.mockk.every
import io.mockk.mockkStatic
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class AuthorRequestTest {

    @Test fun `should convert AuthorRequest to Author entity`() {
        val authorRequest = AuthorRequest(
            name = "Jane Doe",
            email = "jane.doe@gmail.com",
            description = "An example author"
        )

        val fixedInstant = Instant.now()
        mockkStatic(Instant::class) {
            every { Instant.now() } returns fixedInstant

            val expected = with(authorRequest) {
                Author(
                    name = this.name,
                    email = this.email,
                    description = this.description
                )
            }
            val actual = authorRequest.toEntity()

            assertThat(expected).isEqualTo(actual)
        }
    }
}