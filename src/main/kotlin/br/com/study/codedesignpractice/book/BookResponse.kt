package br.com.study.codedesignpractice.book

import java.time.format.DateTimeFormatter
import java.util.*

data class BookResponse(
    val id: UUID?,
    val title: String?,
    val summary: String?,
    val tableOfContents: String?,
    val price: Int?,
    val numberOfPages: Int?,
    val isbn: String?,
    val publishDate: String?,
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
                publishDate = DateTimeFormatter.ofPattern("dd/MM/yyyy").format(this.publishDate)
            )
        }
    }
}
