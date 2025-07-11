package br.com.study.codedesignpractice.category

import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

class CategoryRequestTest {

    @Test
    fun `should be able to convert request into entity`() {
        val categoryName = "romance"
        val categoryRequest = CategoryRequest(name = categoryName)

        val expected = Category(name = categoryName)
        val actual = categoryRequest.toEntity()

        assertThat(expected).isEqualTo(actual)
    }
}