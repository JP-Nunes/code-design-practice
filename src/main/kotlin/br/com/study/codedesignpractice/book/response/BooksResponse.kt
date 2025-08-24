package br.com.study.codedesignpractice.book.response

import br.com.study.codedesignpractice.book.repository.Book
import java.time.format.DateTimeFormatter
import java.util.UUID

data class BooksResponse(
    val data: List<BookResponse>
) {

    companion object {
        fun fromEntity(books: List<Book>) = BooksResponse(books.map { BookResponse.fromEntity(it) })
    }

    data class BookResponse(
        val id: UUID?,
        val title: String?,
        val summary: String?,
        val tableOfContents: String?,
        val price: Int?,
        val numberOfPages: Int?,
        val isbn: String?,
        val publishDate: String?,
        val categoryId: UUID?,
        val authorId: UUID?,
    ) {

        companion object {
            fun fromEntity(book: Book) = with(book) {
                BookResponse(
                    id = this.id,
                    title = this.title,
                    summary = this.summary,
                    tableOfContents = this.tableOfContents,
                    price = this.price,
                    numberOfPages = this.numberOfPages,
                    isbn = this.isbn,
                    publishDate = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(this.publishDate),
                    categoryId = this.category.id,
                    authorId = this.author.id,
                )
            }
        }
    }
}
