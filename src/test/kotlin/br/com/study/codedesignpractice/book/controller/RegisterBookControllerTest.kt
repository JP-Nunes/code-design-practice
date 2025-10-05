package br.com.study.codedesignpractice.book.controller

import br.com.study.codedesignpractice.author.Author
import br.com.study.codedesignpractice.book.repository.BookRepository
import br.com.study.codedesignpractice.book.controller.request.BookRequest
import br.com.study.codedesignpractice.book.controller.response.BookResponse
import br.com.study.codedesignpractice.category.Category
import io.mockk.every
import io.mockk.mockk
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity
import java.net.URI
import java.time.LocalDate
import java.util.*

class RegisterBookControllerTest {

    private lateinit var registerBookController: RegisterBookController

    private lateinit var entityManager: EntityManager
    private lateinit var bookRepository: BookRepository

    @BeforeEach
    fun setUp() {
        entityManager = mockk<EntityManager>()
        bookRepository= mockk<BookRepository>()

        registerBookController = RegisterBookController(bookRepository, entityManager)
    }


    @Test
    fun `should be able to register a book`() {
        val category = category()
        val author = author()
        val bookRequest = bookRequest(categoryId = category.id.toString(), authorId = author.id.toString())

        every { entityManager.find(Category::class.java, UUID.fromString(category.id.toString())) } returns category
        every { entityManager.find(Author::class.java, UUID.fromString(author.id.toString())) } returns author

        val bookEntity = bookRequest.toEntity(entityManager)
        val persistedBookEntity = bookEntity.copy(id = UUID.randomUUID())

        every { bookRepository.save(bookEntity) } returns persistedBookEntity

        val expected = ResponseEntity
            .created(URI("/v1/books/${persistedBookEntity.id}"))
            .body(BookResponse.fromEntity(persistedBookEntity))
        val actual = registerBookController.register(bookRequest)

        Assertions.assertThat(expected).isEqualTo(actual)
    }

    private fun bookRequest(
        categoryId: String,
        authorId: String
    ): BookRequest = BookRequest(
        title = "Effective Kotlin",
        summary = "A book about effective Kotlin practices.",
        tableOfContents = "1. Introduction\n2. Basics",
        price = 30,
        numberOfPages = 200,
        isbn = "123-4567890123",
        publishDate = LocalDate.now().plusDays(1),
        categoryId = categoryId,
        authorId = authorId
    )

    private fun category(): Category = Category(
        name = "romance",
        id = UUID.randomUUID()
    )

    private fun author(): Author = Author(
        name = "John Doe",
        email = "john.doe@outlook.com",
        description = "A lovely author full of love",
        id = UUID.randomUUID()
    )
}