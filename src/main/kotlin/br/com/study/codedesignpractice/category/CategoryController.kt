package br.com.study.codedesignpractice.category

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

const val CATEGORIES_V1_PATH = "/v1/categories"

@RestController
@RequestMapping(CATEGORIES_V1_PATH)
class CategoryController(private val categoryRepository: CategoryRepository) {

    @PostMapping
    fun register(@RequestBody @Valid categoryRequest: CategoryRequest): ResponseEntity<CategoryResponse> {
        val category = categoryRepository.save(categoryRequest.toEntity())
        return ResponseEntity
            .created(URI("$CATEGORIES_V1_PATH/${category.id}"))
            .body(CategoryResponse.fromEntity(category))
    }
}