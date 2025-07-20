package book

import br.com.study.codedesignpractice.CodeDesignPracticeApplication
import br.com.study.codedesignpractice.author.Author
import br.com.study.codedesignpractice.author.AuthorRepository
import br.com.study.codedesignpractice.book.BookRepository
import br.com.study.codedesignpractice.book.BookResponse
import br.com.study.codedesignpractice.category.Category
import br.com.study.codedesignpractice.category.CategoryRepository
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.jsonMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import jakarta.transaction.Transactional
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.skyscreamer.jsonassert.JSONAssert
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.MediaType
import org.springframework.test.json.JsonCompareMode
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MvcResult
import org.springframework.test.web.servlet.post
import java.util.*

@SpringBootTest(
    classes = [CodeDesignPracticeApplication::class],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@AutoConfigureMockMvc
@Transactional
class RegisterBookIntegrationTest(
    @param:Autowired private val mockMvc: MockMvc,
    @param:Autowired private val categoryRepository: CategoryRepository,
    @param:Autowired private val authorRepository: AuthorRepository,
    @param:Autowired private val bookRepository: BookRepository,
) {

    companion object {
        const val BOOKS_V1_PATH = "/v1/books"
    }

    @Nested
    inner class TestRegisterBookSuccess {

        @Test
        fun `should be able to register a book`() {
            val categoryEntity = Category(name = "Non Fiction")
            val authorEntity = author()
            val persistedCategory = categoryRepository.save(categoryEntity)
            val persistedAuthor = authorRepository.save(authorEntity)
            val bookRequest = bookRequest(
                categoryId = persistedCategory.id.toString(),
                authorId = persistedAuthor.id.toString()
            )

            val mockMvcResult = mockMvc.post(BOOKS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = bookRequest
            }
                .andExpect { status { isCreated() } }
                .andDo { print() }
                .andReturn()

            val actualResponse = mockMvcResult.response.contentAsString
            JSONAssert.assertEquals(expectedResponse(mockMvcResult), actualResponse, true)
        }

        fun expectedResponse(mockMvcResult: MvcResult): String {
            val mapper = jsonMapper { addModules(kotlinModule(), JavaTimeModule()) } // TODO("write why this is needed")
            val bookResponse = mapper.readValue(mockMvcResult.response.contentAsString, BookResponse::class.java)
            val persistedBook = bookRepository.findByIdOrNull(bookResponse.id!!)
            assert(value = persistedBook?.id != null) { "The book was not found by the BookResponse id" }

            return """
                {
                  "id": ${persistedBook!!.id},
                  "title": "Refactoring",
                  "summary": "Improving the design of existing code.",
                  "tableOfContents": "Introduction, Principles, Catalog, ...",
                  "price": 60,
                  "numberOfPages": 431,
                  "isbn": "9780135957059",
                  "publishDate": "01/05/2300"
                }
            """.trimIndent()
        }
    }

    @Nested
    inner class TestRegisterBookTitleValidation {

        @Test
        fun `should be able to validate if book title is unique`() {
            val categoryEntity = Category(name = "Fiction")
            val authorEntity = author()
            val persistedCategory = categoryRepository.save(categoryEntity)
            val persistedAuthor = authorRepository.save(authorEntity)
            val bookRequest = bookRequest(
                title = "Refactoring",
                categoryId = persistedCategory.id.toString(),
                authorId = persistedAuthor.id.toString(),
                isbn = "Isbn"
            )

            mockMvc.post(BOOKS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = bookRequest
            }
                .andExpect { status { isCreated() } }
                .andDo { print() }

            val bookRequestWithRepeatedTitle = bookRequest(
                title = "Refactoring",
                categoryId = persistedCategory.id.toString(),
                authorId = persistedAuthor.id.toString(),
                isbn = "OtherIsbn"
            )

            val repeatedBookTitleExpectedResponse = """
              {
                "invalidProperties": ["title"],
                "errorMessages": ["Title already in use"]
              }
            """.trimIndent()

            mockMvc.post(BOOKS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = bookRequestWithRepeatedTitle
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(repeatedBookTitleExpectedResponse, JsonCompareMode.STRICT)
                }
            }.andDo { print() }
        }

        @Test
        fun `should not be able to register a book with a blank title`() {
            val emptyBookTitleRequest = bookRequest(title = "")
            val blankBookTitleRequest = bookRequest(title = " ")
            val nullBookTitleRequest = """
                {
                  "summary": "Improving the design of existing code.",
                  "tableOfContents": "Introduction, Principles, Catalog, ...",
                  "price": 60,
                  "numberOfPages": 431,
                  "isbn": "9780135957059",
                  "publishDate": "01/05/2030",
                  "categoryId": "${UUID.randomUUID()}",
                  "authorId": "${UUID.randomUUID()}"
                }
            """.trimIndent()

            val blankTitleExpectedResponse = """
              {
                "invalidProperties": ["title"],
                "errorMessages": ["must not be blank"]
              }
            """.trimIndent()

            val invalidBookRequests = listOf(emptyBookTitleRequest, blankBookTitleRequest, nullBookTitleRequest)
            invalidBookRequests.forEach { bookRequest ->
                val mockMvcResult = mockMvc.post(BOOKS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = bookRequest
                }
                    .andExpect { status { isBadRequest() } }
                    .andDo { print() }
                    .andReturn()

                val actualResponse = mockMvcResult.response.contentAsString
                JSONAssert.assertEquals(blankTitleExpectedResponse, actualResponse, true)
            }
        }
    }

    @Nested
    inner class TestRegisterBookSummaryValidation {

        @Test
        fun `should not be able to register a book with a blank summary`() {
            val emptyBookSummaryRequest = bookRequest(summary = "")
            val blankBookSummaryRequest = bookRequest(summary = " ")
            val nullBookSummaryRequest = """
                {
                  "title": "Refactoring",
                  "tableOfContents": "Introduction, Principles, Catalog, ...",
                  "price": 60,
                  "numberOfPages": 431,
                  "isbn": "9780135957059",
                  "publishDate": "01/05/2030",
                  "categoryId": "${UUID.randomUUID()}",
                  "authorId": "${UUID.randomUUID()}"
                }
            """.trimIndent()

            val blankSummaryExpectedResponse = """
              {
                "invalidProperties": ["summary"],
                "errorMessages": ["must not be blank"]
              }
            """.trimIndent()

            val invalidBookRequests = listOf(emptyBookSummaryRequest, blankBookSummaryRequest, nullBookSummaryRequest)
            invalidBookRequests.forEach { bookRequest ->
                mockMvc.post(BOOKS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = bookRequest
                }.andExpect {
                    status { isBadRequest() }
                    content {
                        contentType(MediaType.APPLICATION_JSON)
                        json(blankSummaryExpectedResponse, JsonCompareMode.STRICT)
                    }
                }.andDo { print() }
            }
        }

        @Test
        fun `should not be able to register a book with a summary bigger than 500 characters`() {
            val tooLongBookSummaryRequest = bookRequest(summary = "a".repeat(501))
            mockMvc.post(BOOKS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = tooLongBookSummaryRequest
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(tooLongSummaryExpectedResponse, JsonCompareMode.STRICT)
                }
            }.andDo { print() }
        }

        val tooLongSummaryExpectedResponse = """
            {
                "invalidProperties": ["summary"],
                "errorMessages": ["size must be between 0 and 500"]
            }
        """.trimIndent()
    }

    @Nested
    inner class TestRegisterBookPriceValidation {

        @Test
        fun `should not be able to register a book with a null price`() {
            val nullBookTitleRequest = """
                {
                  "title": "Refactoring",
                  "summary": "Improving the design of existing code.",
                  "tableOfContents": "Introduction, Principles, Catalog, ...",
                  "numberOfPages": 431,
                  "isbn": "9780135957059",
                  "publishDate": "01/05/2030",
                  "categoryId": "${UUID.randomUUID()}",
                  "authorId": "${UUID.randomUUID()}"
                }
            """.trimIndent()

            mockMvc.post(BOOKS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = nullBookTitleRequest
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(nullPriceExpectedResponse, JsonCompareMode.STRICT)
                }
            }.andDo { print() }
        }

        val nullPriceExpectedResponse = """
            {
                "invalidProperties": ["price"],
                "errorMessages": ["must not be null"]
            }
        """.trimIndent()

        @Test
        fun `should not be able to register a book with a price lesser than 20`() {
            val underPricedBookRequest = bookRequest(price = 10)
            mockMvc.post(BOOKS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = underPricedBookRequest
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(underPricedBookExpectedResponse, JsonCompareMode.STRICT)
                }
            }.andDo { print() }
        }

        val underPricedBookExpectedResponse = """
            {
                "invalidProperties": ["price"],
                "errorMessages": ["must be greater than or equal to 20"]
            }
        """.trimIndent()
    }

    @Nested
    inner class TestRegisterBookNumberOfPagesValidation {

        @Test
        fun `should not be able to register a book with null number of pages`() {
            val nullBookNumberOfPagesRequest = """
                {
                  "title": "Refactoring",
                  "summary": "Improving the design of existing code.",
                  "tableOfContents": "Introduction, Principles, Catalog, ...",
                  "price": 60,
                  "isbn": "9780135957059",
                  "publishDate": "01/05/2030",
                  "categoryId": "${UUID.randomUUID()}",
                  "authorId": "${UUID.randomUUID()}"
                }
            """.trimIndent()

            val nullNumberOfPagesExpectedResponse = """
                {
                    "invalidProperties": ["numberOfPages"],
                    "errorMessages": ["must not be null"]
                }
            """.trimIndent()

            mockMvc.post(BOOKS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = nullBookNumberOfPagesRequest
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(nullNumberOfPagesExpectedResponse, JsonCompareMode.STRICT)
                }
            }.andDo { print() }
        }

        @Test
        fun `should not be able to register a book with a number of pages lesser than 100`() {
            val tooSmallNumberOfPagesBookRequest = bookRequest(numberOfPages = 10)

            val tooSmallNumberOfPagesExpectedResponse = """
              {
                "invalidProperties": ["numberOfPages"],
                "errorMessages": ["must be greater than or equal to 100"]
              }
            """.trimIndent()

            mockMvc.post(BOOKS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = tooSmallNumberOfPagesBookRequest
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(tooSmallNumberOfPagesExpectedResponse, JsonCompareMode.STRICT)
                }
            }.andDo { print() }
        }
    }

    @Nested
    inner class TestRegisterBookIsbnValidation {

        @Test
        fun `should not be able to register a book with a blank isbn`() {
            val emptyBookIsbnRequest = bookRequest(isbn = "")
            val blankBookIsbnRequest = bookRequest(isbn = " ")
            val nullBookIsbnRequest = """
                {
                  "title": "Refactoring",
                  "summary": "Improving the design of existing code.",
                  "tableOfContents": "Introduction, Principles, Catalog, ...",
                  "price": 60,
                  "numberOfPages": 431,
                  "publishDate": "01/05/2030",
                  "categoryId": "${UUID.randomUUID()}",
                  "authorId": "${UUID.randomUUID()}"
                }
            """.trimIndent()

            val blankIsbnExpectedResponse = """
              {
                "invalidProperties": ["isbn"],
                "errorMessages": ["must not be blank"]
              }
            """.trimIndent()

            val invalidBookRequests = listOf(emptyBookIsbnRequest, blankBookIsbnRequest, nullBookIsbnRequest)
            invalidBookRequests.forEach { bookRequest ->
                val mockMvcResult = mockMvc.post(BOOKS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = bookRequest
                }
                    .andExpect { status { isBadRequest() } }
                    .andDo { print() }
                    .andReturn()

                val actualResponse = mockMvcResult.response.contentAsString
                JSONAssert.assertEquals(blankIsbnExpectedResponse, actualResponse, true)
            }
        }

        @Test
        fun `should not be able to register a book with a number of pages lesser than 100`() {
            val tooSmallNumberOfPagesBookRequest = bookRequest(numberOfPages = 10)

            val tooSmallNumberOfPagesExpectedResponse = """
              {
                "invalidProperties": ["numberOfPages"],
                "errorMessages": ["must be greater than or equal to 100"]
              }
            """.trimIndent()

            mockMvc.post(BOOKS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = tooSmallNumberOfPagesBookRequest
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(tooSmallNumberOfPagesExpectedResponse, JsonCompareMode.STRICT)
                }
            }.andDo { print() }
        }

        @Test
        fun `should be able to validate if book isbn is unique`() {
            val categoryEntity = Category(name = "Adventure")
            val authorEntity = author()
            val persistedCategory = categoryRepository.save(categoryEntity)
            val persistedAuthor = authorRepository.save(authorEntity)
            val repeatedIsbn = "RepeatedIsbn"
            val bookRequest = bookRequest(
                title = "Refactoring",
                categoryId = persistedCategory.id.toString(),
                authorId = persistedAuthor.id.toString(),
                isbn = repeatedIsbn
            )

            mockMvc.post(BOOKS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = bookRequest
            }
                .andExpect { status { isCreated() } }
                .andDo { print() }

            val bookRequestWithRepeatedIsbn = bookRequest(
                title = "Patterns of Enterprise Application Architecture",
                categoryId = persistedCategory.id.toString(),
                authorId = persistedAuthor.id.toString(),
                isbn = repeatedIsbn
            )

            val repeatedBookTitleExpectedResponse = """
              {
                "invalidProperties": ["isbn"],
                "errorMessages": ["Isbn already in use"]
              }
            """.trimIndent()

            mockMvc.post(BOOKS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = bookRequestWithRepeatedIsbn
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(repeatedBookTitleExpectedResponse, JsonCompareMode.STRICT)
                }
            }.andDo { print() }
        }
    }

    @Nested
    inner class TestRegisterBookPublishDateValidation {

        @Test
        fun `should not be able to register a book with null publishDate`() {
            val nullBookPublishDateRequest = """
                {
                  "title": "Refactoring",
                  "summary": "Improving the design of existing code.",
                  "tableOfContents": "Introduction, Principles, Catalog, ...",
                  "price": 60,
                  "isbn": "9780135957059",
                  "numberOfPages": 431,
                  "categoryId": "${UUID.randomUUID()}",
                  "authorId": "${UUID.randomUUID()}"
                }
            """.trimIndent()

            val nullPublishDateExpectedResponse = """
                {
                    "invalidProperties": ["publishDate"],
                    "errorMessages": ["must not be null"]
                }
            """.trimIndent()

            mockMvc.post(BOOKS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = nullBookPublishDateRequest
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(nullPublishDateExpectedResponse, JsonCompareMode.STRICT)
                }
            }.andDo { print() }
        }

        @Test
        fun `should not be able to register a book with publish date in the past`() {
            val bookPublishDateInThePastRequest = bookRequest(publishDate = "01/01/1990")

            val publishDateInThePastExpectedResponse = """
                {
                    "invalidProperties": ["publishDate"],
                    "errorMessages": ["must be a future date"]
                }
            """.trimIndent()

            mockMvc.post(BOOKS_V1_PATH) {
                contentType = MediaType.APPLICATION_JSON
                content = bookPublishDateInThePastRequest
            }.andExpect {
                status { isBadRequest() }
                content {
                    contentType(MediaType.APPLICATION_JSON)
                    json(publishDateInThePastExpectedResponse, JsonCompareMode.STRICT)
                }
            }.andDo { print() }
        }
    }

    @Nested
    inner class TestRegisterBookCategoryValidation {

        @Test
        fun `should not be able to register a book with a blank category`() {
            val emptyBookCategoryRequest = bookRequest(categoryId = "")
            val blankBookCategoryRequest = bookRequest(categoryId = " ")
            val nullBookCategoryRequest = """
                {
                  "title": "Refactoring",
                  "summary": "Improving the design of existing code.",
                  "tableOfContents": "Introduction, Principles, Catalog, ...",
                  "price": 60,
                  "isbn": "9780135957059",
                  "numberOfPages": 431,
                  "publishDate": "01/05/2300",
                  "authorId": "${UUID.randomUUID()}"
                }
            """.trimIndent()

            val blankCategoryExpectedResponse = """
                {
                    "invalidProperties": ["categoryId"],
                    "errorMessages": ["must not be blank"]
                }
            """.trimIndent()

            val invalidBookRequests = listOf(emptyBookCategoryRequest, blankBookCategoryRequest, nullBookCategoryRequest)
            invalidBookRequests.forEach { bookRequest ->
                val mockMvcResult = mockMvc.post(BOOKS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = bookRequest
                }
                    .andExpect { status { isBadRequest() } }
                    .andDo { print() }
                    .andReturn()

                val actualResponse = mockMvcResult.response.contentAsString
                JSONAssert.assertEquals(blankCategoryExpectedResponse, actualResponse, true)
            }
        }
    }

    @Nested
    inner class TestRegisterBookAuthorValidation {

        @Test
        fun `should not be able to register a book with blank author id`() {
            val emptyBookAuthorRequest = bookRequest(authorId = "")
            val blankBookAuthorRequest = bookRequest(authorId = " ")
            val nullBookAuthorRequest = """
                {
                  "title": "Refactoring",
                  "summary": "Improving the design of existing code.",
                  "tableOfContents": "Introduction, Principles, Catalog, ...",
                  "price": 60,
                  "isbn": "9780135957059",
                  "numberOfPages": 431,
                  "publishDate": "01/05/2300",
                  "categoryId": "${UUID.randomUUID()}"
                }
            """.trimIndent()

            val blankAuthorExpectedResponse = """
                {
                    "invalidProperties": ["authorId"],
                    "errorMessages": ["must not be blank"]
                }
            """.trimIndent()

            val invalidBookRequests = listOf(emptyBookAuthorRequest, blankBookAuthorRequest, nullBookAuthorRequest)
            invalidBookRequests.forEach { bookRequest ->
                val mockMvcResult = mockMvc.post(BOOKS_V1_PATH) {
                    contentType = MediaType.APPLICATION_JSON
                    content = bookRequest
                }
                    .andExpect { status { isBadRequest() } }
                    .andDo { print() }
                    .andReturn()

                val actualResponse = mockMvcResult.response.contentAsString
                JSONAssert.assertEquals(blankAuthorExpectedResponse, actualResponse, true)
            }
        }
    }

    private fun author() = Author(
        name = "Martin Fowler",
        email = "martin.fowler@gmail.com",
        description = "Software development guide"
    )

    private fun bookRequest(
        title: String = "Refactoring",
        summary: String = "Improving the design of existing code.",
        price: Long = 60,
        numberOfPages: Int = 431,
        isbn: String = "9780135957059",
        publishDate: String = "01/05/2300",
        categoryId: String = UUID.randomUUID().toString(),
        authorId: String = UUID.randomUUID().toString(),
    ) = """
        {
          "title": "$title",
          "summary": "$summary",
          "tableOfContents": "Introduction, Principles, Catalog, ...",
          "price": $price,
          "numberOfPages": $numberOfPages,
          "isbn": "$isbn",
          "publishDate": "$publishDate",
          "categoryId": "$categoryId",
          "authorId": "$authorId"
        }
    """.trimIndent()
}