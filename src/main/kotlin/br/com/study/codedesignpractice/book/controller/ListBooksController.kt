package br.com.study.codedesignpractice.book.controller

import br.com.study.codedesignpractice.book.repository.BookRepository
import br.com.study.codedesignpractice.book.response.BooksResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping(BOOKS_V1_PATH)
class ListBooksController(private val bookRepository: BookRepository) {

    @GetMapping
    fun list(): ResponseEntity<BooksResponse> {
        val books = bookRepository.findAll().toList()
        return ResponseEntity.ok(BooksResponse.fromEntity(books))
    }
}