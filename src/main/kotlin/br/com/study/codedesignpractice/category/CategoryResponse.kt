package br.com.study.codedesignpractice.category

import java.util.UUID

data class CategoryResponse(val name: String, val id: UUID?) {

    companion object {
        fun fromEntity(category: Category) = with(category) {
            CategoryResponse(name = category.name, id = category.id)
        }
    }
}