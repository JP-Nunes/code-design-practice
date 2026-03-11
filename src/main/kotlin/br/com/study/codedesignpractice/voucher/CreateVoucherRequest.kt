package br.com.study.codedesignpractice.voucher

import com.fasterxml.jackson.annotation.JsonFormat
import jakarta.validation.constraints.Future
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import java.math.BigDecimal
import java.time.LocalDate

data class CreateVoucherRequest(
    @field:NotBlank
    val code: String?,

    @field:NotNull
    @field:Positive
    val discount: BigDecimal?,

    @field:NotNull
    @field:Future
    @field:JsonFormat(pattern = "dd/MM/yyyy")
    val expirationDate: LocalDate?
) {
    fun toEntity() = Voucher(
        code = this.code,
        discount = this.discount,
        expirationDate = this.expirationDate
    )
}