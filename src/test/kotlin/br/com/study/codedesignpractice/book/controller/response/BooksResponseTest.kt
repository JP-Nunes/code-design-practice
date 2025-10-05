package br.com.study.codedesignpractice.book.controller.response

import br.com.study.codedesignpractice.author.Author
import br.com.study.codedesignpractice.book.repository.Book
import br.com.study.codedesignpractice.category.Category
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.UUID

class BooksResponseTest {

    @Test
    fun `should be able to create a BooksResponse from a list of Book entities`() {
        val books = listOf(book(), book(), book())
        val expectedBooksResponseData = books.map {
            BooksResponse.BookResponse(
                id = it.id,
                title = it.title,
                summary = it.summary,
                tableOfContents = it.tableOfContents,
                price = it.price,
                numberOfPages = it.numberOfPages,
                isbn = it.isbn,
                publishDate = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(it.publishDate),
                categoryId = it.category.id,
                authorId = it.author.id,
            )
        }
        val expected = BooksResponse(data = expectedBooksResponseData)

        val actual = BooksResponse.fromEntity(books)

        assertEquals(expected, actual)
    }

    private fun author(): Author = Author(
        name = "John Doe",
        email = "john.doe@hotmail.com",
        description = "A sample author",
        UUID.randomUUID()
    )

    private fun category(): Category = Category(name = "Non Fiction", id = UUID.randomUUID())

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
}
