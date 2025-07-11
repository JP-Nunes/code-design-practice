package br.com.study.codedesignpractice.category

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface CategoryRepository : CrudRepository<Category, UUID> {

    fun existsByName(name: String): Boolean
}