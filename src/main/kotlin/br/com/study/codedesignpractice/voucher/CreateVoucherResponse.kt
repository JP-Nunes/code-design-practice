package br.com.study.codedesignpractice.voucher

import com.fasterxml.jackson.annotation.JsonFormat
import java.math.BigDecimal
import java.time.LocalDate

data class CreateVoucherResponse(
    val code: String,
    val discount: BigDecimal,

    @field:JsonFormat(pattern = "dd/MM/yyyy")
    val expirationDate: LocalDate
)

fun Voucher.toCreateVoucherResponse() = CreateVoucherResponse(
    code = this.code ?: throw IllegalStateException("Voucher code is mandatory"),
    discount = this.discount ?: throw IllegalStateException("Voucher discount is mandatory"),
    expirationDate = this.expirationDate ?: throw IllegalStateException("Voucher expirationDate is mandatory"),
)