package br.com.study.codedesignpractice.location

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface CountryRepository : CrudRepository<Country, UUID>