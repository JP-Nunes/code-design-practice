package br.com.study.codedesignpractice.author

import java.util.UUID

data class AuthorResponse(val id: UUID?) {

    companion object {
        fun fromEntity(author: Author) = AuthorResponse(author.id)
    }
}
