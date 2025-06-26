package br.com.study.codedesignpractice.author

data class AuthorRequest(
    val name: String,
    val email: String,
    val description: String
) {

    fun toEntity() = Author(
        name = name,
        email = email,
        description = description
    )
}
