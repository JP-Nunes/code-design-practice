package br.com.study.codedesignpractice.category

import br.com.study.codedesignpractice.validator.Unique
import jakarta.validation.constraints.NotBlank

data class CategoryRequest(
    @get:NotBlank
    @get:Unique(entityClass = Category::class, fieldName = "name", message = "Category name must be unique")
    val name: String
) {
    fun toEntity() = Category(name = this.name)
}
