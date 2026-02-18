package br.com.study.codedesignpractice.book.repository

import jakarta.persistence.EntityManager
import java.util.UUID

fun EntityManager.findBooksByIds(ids: List<UUID>): List<Book> {
    if (ids.isEmpty()) return emptyList()

    return this.createQuery(
        "SELECT b FROM Book b WHERE b.id IN :ids",
        Book::class.java
    )
        .setParameter("ids", ids)
        .resultList
}