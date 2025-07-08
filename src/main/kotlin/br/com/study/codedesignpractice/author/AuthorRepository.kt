package br.com.study.codedesignpractice.author

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface AuthorRepository: CrudRepository<Author, UUID> {

    fun existsByEmail(email: String): Boolean
}