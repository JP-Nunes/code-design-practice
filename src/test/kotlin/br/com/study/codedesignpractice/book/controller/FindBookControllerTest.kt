package br.com.study.codedesignpractice.book.controller

import br.com.study.codedesignpractice.author.Author
import br.com.study.codedesignpractice.book.controller.response.BookResponse
import br.com.study.codedesignpractice.book.repository.Book
import br.com.study.codedesignpractice.book.repository.BookRepository
import br.com.study.codedesignpractice.book.controller.response.BooksResponse
import br.com.study.codedesignpractice.category.Category
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import java.time.LocalDate
import java.util.*

class FindBookControllerTest {

    private lateinit var findBookController: FindBookController
    private lateinit var bookRepository: BookRepository

    @BeforeEach
    fun setUp() {
        bookRepository= mockk<BookRepository>()
        findBookController = FindBookController(bookRepository)
    }

    @Nested
    inner class ListAllBooks {

        @Test
        fun `should be able to list all books`() {
            val books = listOf(book(), book(), book()).asIterable()

            every { bookRepository.findAll() } returns books

            val expected = ResponseEntity.ok(BooksResponse.fromEntity(books.toList()))
            val actual = findBookController.list()

            Assertions.assertThat(expected).isEqualTo(actual)
        }
    }

    @Nested
    inner class FindBookById {

        @Test
        fun `should be able to find a book by its id`() {
            val book = book()

            every { bookRepository.findByIdOrNull(book.id!!) } returns book

            val expected = ResponseEntity.ok(BookResponse.fromEntity(book))
            val actual = findBookController.findBy(book.id!!)

            Assertions.assertThat(expected).isEqualTo(actual)
        }

        @Test
        fun `should return not found when book was not found by the provided id`() {
            val book = book()

            every { bookRepository.findByIdOrNull(book.id!!) } returns null

            val expected: ResponseEntity<BookResponse> = ResponseEntity.notFound().build()
            val actual = findBookController.findBy(book.id!!)

            Assertions.assertThat(expected).isEqualTo(actual)
        }
    }


    private fun book(
        category: Category = category(),
        author: Author = author()
    ): Book = Book(
        title = "Book One",
        summary = "First book summary",
        tableOfContents = "TOC 1",
        price = 300,
        numberOfPages = 200,
        isbn = "111-111-111",
        publishDate = LocalDate.now().plusDays(15),
        category = category,
        author = author,
        id = UUID.randomUUID()
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