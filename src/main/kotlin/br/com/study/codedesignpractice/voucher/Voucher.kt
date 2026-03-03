package br.com.study.codedesignpractice.voucher

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDate

@Entity
class Voucher(
    @field:NotBlank
    val code: String?,

    @field:NotNull
    @field:Positive
    val discount: BigDecimal?,

    @field:NotNull
    @field:Future
    val expirationDate: LocalDate?,

    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null
)
