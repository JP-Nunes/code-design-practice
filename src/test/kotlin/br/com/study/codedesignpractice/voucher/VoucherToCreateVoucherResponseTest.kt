package br.com.study.codedesignpractice.voucher

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.time.LocalDate
import kotlin.test.assertEquals

class VoucherToCreateVoucherResponseTest {

    @Test
    fun `should be able to convert Voucher into CreateVoucherResponse`() {
        val voucher = voucher()

        val expected = with(voucher) {
            CreateVoucherResponse(
                code = this.code!!,
                discount = this.discount!!,
                expirationDate = this.expirationDate!!
            )
        }
        val actual = voucher.toCreateVoucherResponse()

        assertEquals(expected, actual)
    }

    @Test
    fun `should throw IllegalStateException when any required field is null`() {
        val voucher = voucher()

        val codeException = assertThrows<IllegalStateException> {
            voucher.copy(code = null).toCreateVoucherResponse()
        }
        assertEquals("Voucher code is mandatory", codeException.message)

        val discountException = assertThrows<IllegalStateException> {
            voucher.copy(discount = null).toCreateVoucherResponse()
        }
        assertEquals("Voucher discount is mandatory", discountException.message)

        val expirationDateException = assertThrows<IllegalStateException> {
            voucher.copy(expirationDate = null).toCreateVoucherResponse()
        }
        assertEquals("Voucher expirationDate is mandatory", expirationDateException.message)
    }

    private fun voucher() = Voucher(
        code = "PROMO-10",
        discount = BigDecimal(0.1),
        expirationDate = LocalDate.now().plusDays(1),
        id = 1
    )
}