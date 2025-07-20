package br.com.study.codedesignpractice.book

import br.com.study.codedesignpractice.author.Author
import br.com.study.codedesignpractice.category.Category
import br.com.study.codedesignpractice.validator.Unique
import com.fasterxml.jackson.annotation.JsonFormat
import com.fasterxml.jackson.annotation.JsonFormat.*
import jakarta.persistence.EntityManager
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import java.time.LocalDate
import java.util.UUID

data class BookRequest(
    @field:NotBlank
    @field:Unique(entityClass = Book::class, fieldName = "title", message = "Title already in use")
    val title: String?,

    @field:NotBlank
    @field:Size(max = 500)
    val summary: String?,

    val tableOfContents: String?,

    @field:NotNull
    @field:Min(20)
    val price: Int?,

    @field:NotNull
    @field:Min(100)
    val numberOfPages: Int?,

    @field:NotBlank
    @field:Unique(entityClass = Book::class, fieldName = "isbn", message = "Isbn already in use")
    val isbn: String?,

    @field:Future
    @field:NotNull
    @field:JsonFormat(pattern = "dd/MM/yyyy", shape = Shape.STRING)
    val publishDate: LocalDate?,

    @field:NotBlank
    val categoryId: String?,

    @field:NotBlank
    val authorId: String?
) {

    fun toEntity(entityManager: EntityManager): Book {
        val category = entityManager.find(
            Category::class.java,
            UUID.fromString(this.categoryId)
        ).also { assert(value = it != null) { "Category not found" } }

        val author = entityManager.find(
            Author::class.java,
            UUID.fromString(this.authorId)
        ).also { assert(value = it != null) { "Author not found" } }

        return Book(
            title = title,
            summary = summary,
            tableOfContents = tableOfContents,
            price = price,
            numberOfPages = numberOfPages,
            isbn = isbn,
            publishDate = publishDate,
            category = category,
            author = author
        )
    }
}
