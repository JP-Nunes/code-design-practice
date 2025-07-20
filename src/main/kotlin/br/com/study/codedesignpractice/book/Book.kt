package br.com.study.codedesignpractice.book

import br.com.study.codedesignpractice.author.Author
import br.com.study.codedesignpractice.category.Category
import jakarta.persistence.*
import jakarta.validation.constraints.*
import java.time.LocalDate
import java.util.UUID

@Entity
data class Book(
    @field:NotBlank
    val title: String?,

    @field:NotBlank
    @field:Size(max = 500)
    val summary: String?,

    @Lob
    @field:NotBlank
    val tableOfContents: String?,

    @field:Min(20)
    val price: Int?,

    @field:Min(100)
    val numberOfPages: Int?,

    @field:NotBlank
    val isbn: String?,

    @field:Future
    val publishDate: LocalDate?,

    @ManyToOne
    val category: Category,

    @ManyToOne
    val author: Author,

    @Id
    @GeneratedValue
    val id: UUID? = null
)