package br.com.study.codedesignpractice.voucher

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class CreateVoucherRequestTest {

    @Test
    fun `should be able to convert CreateVoucherRequest to Voucher entity`() {
        val createVoucherRequest = CreateVoucherRequest(
            code = "PROMO-10",
            discount = BigDecimal(0.1),
            expirationDate = LocalDate.now().plusDays(1)
        )

        val expected = createVoucherRequest.let {
            Voucher(
                code = it.code,
                discount = it.discount,
                expirationDate = it.expirationDate
            )
        }
        val actual = createVoucherRequest.toEntity()

        assertEquals(expected, actual)
    }
}