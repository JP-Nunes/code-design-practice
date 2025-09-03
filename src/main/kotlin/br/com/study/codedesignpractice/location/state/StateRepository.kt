package br.com.study.codedesignpractice.location.state

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface StateRepository : CrudRepository<State, UUID>