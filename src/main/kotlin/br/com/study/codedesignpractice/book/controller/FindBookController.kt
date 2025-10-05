package br.com.study.codedesignpractice.book.controller

import br.com.study.codedesignpractice.book.controller.response.BookResponse
import br.com.study.codedesignpractice.book.controller.response.BooksResponse
import br.com.study.codedesignpractice.book.repository.BookRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.*

@RestController
@RequestMapping(BOOKS_V1_PATH)
class FindBookController(private val bookRepository: BookRepository) {

    @GetMapping
    fun list(): ResponseEntity<BooksResponse> {
        val books = bookRepository.findAll().toList()
        return ResponseEntity.ok(BooksResponse.fromEntity(books))
    }

    @GetMapping("/{id}")
    fun findBy(@PathVariable id: UUID): ResponseEntity<BookResponse> {
        val book = bookRepository.findByIdOrNull(id)
        return book?.let { ResponseEntity.ok(BookResponse.fromEntity(it)) } ?: ResponseEntity.notFound().build()
    }
}