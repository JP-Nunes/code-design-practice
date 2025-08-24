package br.com.study.codedesignpractice.book.controller

import br.com.study.codedesignpractice.book.repository.BookRepository
import br.com.study.codedesignpractice.book.request.BookRequest
import br.com.study.codedesignpractice.book.response.BookResponse
import jakarta.persistence.EntityManager
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

const val BOOKS_V1_PATH = "/v1/books"

@RestController
@RequestMapping(BOOKS_V1_PATH)
class RegisterBookController(
    private val bookRepository: BookRepository,
    private val entityManager: EntityManager
) {

    @PostMapping
    fun register(@RequestBody @Valid bookRequest: BookRequest): ResponseEntity<BookResponse> {
        val book = bookRepository.save(bookRequest.toEntity(entityManager))
        return ResponseEntity
            .created(URI("$BOOKS_V1_PATH/${book.id}"))
            .body(BookResponse.fromEntity(book))
    }
}