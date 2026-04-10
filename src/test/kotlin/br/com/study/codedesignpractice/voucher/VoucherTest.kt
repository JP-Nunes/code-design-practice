package br.com.study.codedesignpractice.voucher

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate

class VoucherTest {

    @Nested
    inner class DiscountMultiplierProperty {

        @Test
        fun `should be 1_0000 when discount is 0 percent`() {
            val voucher = voucher(percent = bd("0"))

            assertThat(voucher.discountMultiplier).isEqualByComparingTo(bd("1.0000"))
        }

        @Test
        fun `should be 0_9000 when discount is 10 percent`() {
            val voucher = voucher(percent = bd("10"))

            assertThat(voucher.discountMultiplier).isEqualByComparingTo(bd("0.9000"))
        }

        @Test
        fun `should be 0_0000 when discount is 100 percent`() {
            val voucher = voucher(percent = bd("100"))

            assertThat(voucher.discountMultiplier).isEqualByComparingTo(bd("0.0000"))
        }

        @Test
        fun `should clamp to 0_0000 when discount above 100 percent`() {
            val voucher = voucher(percent = bd("150"))

            assertThat(voucher.discountMultiplier).isEqualByComparingTo(bd("0.0000"))
        }

        @Test
        fun `should round multiplier to 4 decimal places HALF_UP`() {
            val voucher = voucher(percent = bd("12.345"))
            // 1 - 0.12345 = 0.87655 -> HALF_UP to 4 decimals = 0.8766
            assertThat(voucher.discountMultiplier).isEqualByComparingTo(bd("0.88"))
        }
    }

    private fun bd(s: String) = BigDecimal(s)

    private fun voucher(percent: BigDecimal) = Voucher(
        code = "TEST",
        discount = percent,
        expirationDate = LocalDate.now().plusDays(10)
    )
}
