package br.com.study.codedesignpractice.author

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

@RestController("/authors")
class AuthorController(private val authorRepository: AuthorRepository) {

    @PostMapping
    fun registerAuthor(authorRequest: AuthorRequest): ResponseEntity<AuthorResponse> {
        val author = authorRepository.save(authorRequest.toEntity())
        return ResponseEntity
            .created(URI("/authors/${author.id}"))
            .body(AuthorResponse.fromEntity(author))
    }
}