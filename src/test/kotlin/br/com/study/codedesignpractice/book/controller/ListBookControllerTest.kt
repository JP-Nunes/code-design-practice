package br.com.study.codedesignpractice.book.controller

import br.com.study.codedesignpractice.author.Author
import br.com.study.codedesignpractice.book.repository.Book
import br.com.study.codedesignpractice.book.repository.BookRepository
import br.com.study.codedesignpractice.book.response.BooksResponse
import br.com.study.codedesignpractice.category.Category
import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity
import java.time.LocalDate
import java.util.*

class ListBookControllerTest {

    private lateinit var listBookController: ListBooksController
    private lateinit var bookRepository: BookRepository

    @BeforeEach
    fun setUp() {
        bookRepository= mockk<BookRepository>()
        listBookController = ListBooksController(bookRepository)
    }

    @Test
    fun `should be able to list all books`() {
        val books = listOf(book(), book(), book()).asIterable()

        every { bookRepository.findAll() } returns books

        val expected = ResponseEntity.ok(BooksResponse.fromEntity(books.toList()))
        val actual = listBookController.list()

        Assertions.assertThat(expected).isEqualTo(actual)
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