package br.com.study.codedesignpractice.category

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.util.UUID

class CategoryResponseTest {

    @Test
    fun `should be able to create a response from entity`() {
        val category = Category(name = "comedy", id = UUID.randomUUID())

        val expected = CategoryResponse(name = category.name, id = category.id)
        val actual = CategoryResponse.fromEntity(category)

        assertThat(expected).isEqualTo(actual)
    }

}