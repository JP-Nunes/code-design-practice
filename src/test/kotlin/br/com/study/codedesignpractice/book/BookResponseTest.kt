package br.com.study.codedesignpractice.book

import br.com.study.codedesignpractice.author.Author
import br.com.study.codedesignpractice.category.Category
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class BookResponseTest {

    @Test
    fun `should be able to create a BookResponse from Book entity`() {
        val bookEntity = Book(
            title = "Book Title",
            summary = "Book summary",
            tableOfContents = "Markdown table of contents",
            price = 250,
            numberOfPages = 150,
            isbn = "123-456-789",
            publishDate = LocalDate.now().plusDays(10),
            category = Category(name = "Non Fiction"),
            author = Author(
                name = "John Doe",
                email = "john.doe@hotmail.com",
                description = "A sample author"
            )
        )

        val expected = with(bookEntity) {
            BookResponse(
                id = this.id,
                title = this.title,
                summary = this.summary,
                tableOfContents = this.tableOfContents,
                price = this.price,
                numberOfPages = this.numberOfPages,
                isbn = this.isbn,
                publishDate = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(this.publishDate)
            )
        }

        val actual = BookResponse.fromEntity(bookEntity)

        assertEquals(expected, actual)
    }

}