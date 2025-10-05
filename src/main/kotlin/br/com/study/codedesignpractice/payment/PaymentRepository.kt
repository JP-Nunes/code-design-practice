package br.com.study.codedesignpractice.payment

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PaymentRepository : CrudRepository<Payment, UUID>
