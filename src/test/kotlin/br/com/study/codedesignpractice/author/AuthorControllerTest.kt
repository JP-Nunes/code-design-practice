package br.com.study.codedesignpractice.author

import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity
import java.net.URI
import java.time.Instant
import java.util.UUID

class AuthorControllerTest {

    private lateinit var authorRepository: AuthorRepository
    private lateinit var authorController: AuthorController

    @BeforeEach
    fun setUp() {
         authorRepository = mockk<AuthorRepository>()
         authorController = AuthorController(authorRepository)
    }

    @Test
    fun `should be able to register an author`() {
        val authorRequest = AuthorRequest(
            name = "John Doe",
            email = "john.doe@gmail.com",
            description = "A sample author description.",
        )

        val fixedInstant = Instant.now()
        mockkStatic(Instant::class) {
            every { Instant.now() } returns fixedInstant

            val authorBeforePersistence = authorRequest.toEntity()
            val authorAfterPersistence= authorBeforePersistence.copy(id = UUID.randomUUID())
            every { authorRepository.save(authorBeforePersistence) } returns authorAfterPersistence

            val actual = authorController.registerAuthor(authorRequest)
            val expected = ResponseEntity
                .created(URI("/authors/${authorAfterPersistence.id}"))
                .body(AuthorResponse.fromEntity(authorAfterPersistence))

            assertThat(actual.statusCode).isEqualTo(expected.statusCode)
            assertThat(actual.body).isEqualTo(expected.body)
        }
    }
}