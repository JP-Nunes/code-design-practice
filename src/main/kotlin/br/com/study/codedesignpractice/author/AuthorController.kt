package br.com.study.codedesignpractice.author

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

const val AUTHORS_V1_PATH = "/v1/authors"

@RestController
@RequestMapping(AUTHORS_V1_PATH)
class AuthorController(private val authorRepository: AuthorRepository) {

    @PostMapping
    fun register(@RequestBody @Valid authorRequest: AuthorRequest): ResponseEntity<AuthorResponse> {
        val author = authorRepository.save(authorRequest.toEntity())
        return ResponseEntity
            .created(URI("$AUTHORS_V1_PATH/${author.id}"))
            .body(AuthorResponse.fromEntity(author))
    }
}