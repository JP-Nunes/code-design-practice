package br.com.study.codedesignpractice.author

import br.com.study.codedesignpractice.validator.Unique
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class AuthorRequest(
    @get:NotBlank
    val name: String,

    @get:NotBlank
    @get:Email
    @get:Unique(entityClass = Author::class, fieldName = "email", message = "Email already in use")
    val email: String,

    @get:NotBlank
    @get:Size(max = 400)
    val description: String
) {

    fun toEntity() = Author(
        name = name,
        email = email,
        description = description
    )
}
