package book

import book.ListBookIntegrationTest.Companion.BOOKS_V1_PATH
import br.com.study.codedesignpractice.CodeDesignPracticeApplication
import br.com.study.codedesignpractice.author.Author
import br.com.study.codedesignpractice.author.AuthorRepository
import br.com.study.codedesignpractice.book.controller.response.BookResponse
import br.com.study.codedesignpractice.book.repository.Book
import br.com.study.codedesignpractice.book.repository.BookRepository
import br.com.study.codedesignpractice.category.Category
import br.com.study.codedesignpractice.category.CategoryRepository
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get
import java.time.LocalDate
import java.util.*

@SpringBootTest(
    classes = [CodeDesignPracticeApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@Transactional
class FindBookByIdIntegrationTest(
    @param:Autowired private val mockMvc: MockMvc,
    @param:Autowired private val bookRepository: BookRepository,
    @param:Autowired private val categoryRepository: CategoryRepository,
    @param:Autowired private val authorRepository: AuthorRepository
) {

    @Test
    fun `should be able to find a book by its id`() {
        val persistedBook = persistBook()

        val mockMvcResult = mockMvc.get(BOOKS_V1_PATH + "/${persistedBook.id}")
            .andExpect { status { isOk()} }
            .andReturn()

        val expectedResponse = BookResponse.fromEntity(persistedBook).writeAsJson()
        val actualResponse = mockMvcResult.response.contentAsString
        JSONAssert.assertEquals(expectedResponse, actualResponse, true)
    }

    @Test
    fun `should return not found when book was not found by the provided id`() {
        mockMvc.get(BOOKS_V1_PATH + "/${UUID.randomUUID()}").andExpect { status { isNotFound()} }
    }

    private fun persistBook(): Book {
        val category = category()
        val author = author()

        val persistedCategory = categoryRepository.save(category)
        val persistedAuthor = authorRepository.save(author)

        return bookRepository.save(book(persistedCategory, persistedAuthor))
    }

    private fun book(
        category: Category,
        author: Author
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
    )

    private fun category(): Category = Category(name = "romance")

    private fun author(): Author = Author(
        name = "John Doe",
        email = "john.doe@outlook.com",
        description = "A lovely author full of love",
    )
}