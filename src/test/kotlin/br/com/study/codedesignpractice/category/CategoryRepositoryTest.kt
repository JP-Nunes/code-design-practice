package br.com.study.codedesignpractice.category

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.repository.findByIdOrNull
import kotlin.test.assertFalse
import kotlin.test.assertTrue

@DataJpaTest
class CategoryRepositoryTest @Autowired constructor(
    val entityManager: TestEntityManager,
    val categoryRepository: CategoryRepository
) {

    @Test
    fun `should retrieve category with correct generated id and timestamp`() {
        val category = Category(name = "drama")

        entityManager.persist(category)
        entityManager.flush()

        val retrievedCategory = categoryRepository.findByIdOrNull(category.id!!)
        assertThat(retrievedCategory).isEqualTo(category)
    }

    @Test
    fun `should return the correct boolean when category name is or is not repeated`() {
        val category = Category(name = "drama")

        assertFalse { categoryRepository.existsByName(category.name) }

        entityManager.persist(category)
        entityManager.flush()

        assertTrue { categoryRepository.existsByName(category.name) }
    }
}