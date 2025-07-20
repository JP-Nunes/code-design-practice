package br.com.study.codedesignpractice.book

import br.com.study.codedesignpractice.author.Author
import br.com.study.codedesignpractice.author.AuthorRepository
import br.com.study.codedesignpractice.category.Category
import br.com.study.codedesignpractice.category.CategoryRepository
import org.assertj.core.api.Assertions.assertThat
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.repository.findByIdOrNull
import java.time.LocalDate
import kotlin.test.Test

@DataJpaTest
class BookRepositoryTest @Autowired constructor(
    val entityManager: TestEntityManager,
    val categoryRepository: CategoryRepository,
    val authorRepository: AuthorRepository,
    val bookRepository: BookRepository
) {

    @Test
    fun `should be able to register a book`() {
        val category = Category(name = "fiction")
        val author = Author(
            name = "John Doe",
            email = "john.doe@hotmail.com",
            description = "A sample author"
        )

        entityManager.persist(category)
        entityManager.persist(author)
        entityManager.flush()

        val persistedCategory = categoryRepository.findByIdOrNull(category.id!!)
        val persistedAuthor = authorRepository.findByIdOrNull(author.id!!)

        val book = Book(
            title = "Book Title",
            summary = "Book summary",
            tableOfContents = "Markdown table of contents",
            price = 250,
            numberOfPages = 150,
            isbn = "123-456-789",
            publishDate = LocalDate.now().plusDays(10),
            category = persistedCategory!!,
            author = persistedAuthor!!
        )

        entityManager.persist(book)
        entityManager.flush()
        val persistedBook = bookRepository.findByIdOrNull(book.id!!)

        assertThat(persistedBook).isEqualTo(book)
    }
}