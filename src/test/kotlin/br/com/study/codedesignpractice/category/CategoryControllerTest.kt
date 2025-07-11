package br.com.study.codedesignpractice.category

import io.mockk.every
import io.mockk.mockk
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.ResponseEntity
import java.net.URI
import java.util.UUID

class CategoryControllerTest {

    private lateinit var categoryRepository: CategoryRepository
    private lateinit var categoryController: CategoryController

    @BeforeEach
    fun setUp() {
        categoryRepository = mockk<CategoryRepository>()
        categoryController = CategoryController(categoryRepository)
    }

    @Test
    fun `should be able to register a new category`() {
        val categoryRequest = CategoryRequest(name = "action")
        val categoryBeforePersistence = categoryRequest.toEntity()
        val categoryAfterPersistence = categoryBeforePersistence.copy(id = UUID.randomUUID())
        every { categoryRepository.save(categoryBeforePersistence) } returns categoryAfterPersistence

        val expected = ResponseEntity
            .created(URI("/v1/categories/${categoryAfterPersistence.id}"))
            .body(CategoryResponse.fromEntity(category = categoryAfterPersistence))
        val actual = categoryController.register(categoryRequest)

        assertThat(actual.statusCode).isEqualTo(expected.statusCode)
        assertThat(actual.body).isEqualTo(expected.body)
    }
}