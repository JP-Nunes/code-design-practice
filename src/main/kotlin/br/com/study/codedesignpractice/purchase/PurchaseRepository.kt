package br.com.study.codedesignpractice.purchase

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PurchaseRepository : CrudRepository<Purchase, UUID>
