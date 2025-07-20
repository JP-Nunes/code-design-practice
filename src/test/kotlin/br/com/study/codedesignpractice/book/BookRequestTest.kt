package br.com.study.codedesignpractice.book

import br.com.study.codedesignpractice.author.Author
import br.com.study.codedesignpractice.category.Category
import io.mockk.every
import io.mockk.mockk
import jakarta.persistence.EntityManager
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.*

class BookRequestTest {

    private lateinit var entityManager: EntityManager

    @BeforeEach
    fun setUp() {
        entityManager = mockk<EntityManager>()
    }

    @Test
    fun `should be able to convert request into entity`() {
        val category = category()
        val author = author()
        val bookRequest = bookRequest(categoryId = category.id.toString(), authorId = author.id.toString())

        every { entityManager.find(Category::class.java, category.id.toString()) } returns category
        every { entityManager.find(Author::class.java, author.id.toString()) } returns author

        val expected = with(bookRequest) {
            Book(
                title = this.title,
                summary = this.summary,
                tableOfContents = this.tableOfContents,
                price = this.price,
                numberOfPages = this.numberOfPages,
                isbn = this.isbn,
                publishDate = this.publishDate,
                category = category,
                author = author
            )
        }
        val actual = bookRequest.toEntity(entityManager)

        assertThat(expected).isEqualTo(actual)
    }

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
}
