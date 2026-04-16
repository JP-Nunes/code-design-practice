package br.com.study.codedesignpractice.purchase

import br.com.study.codedesignpractice.location.country.Country
import br.com.study.codedesignpractice.location.state.State
import br.com.study.codedesignpractice.voucher.Voucher
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.time.LocalDate
import java.util.UUID

class PurchaseTest {

    @Nested
    inner class TotalWithDiscountPercentage {

        @Test
        fun `should apply 10 percent discount`() {
            val purchase = purchase(total = BigDecimal("100.00"), percent = BigDecimal("10"))

            assertThat(purchase.totalWithDiscount).isEqualByComparingTo(BigDecimal("90.00"))
        }

        @Test
        fun `should keep same total when discount is 0 percent`() {
            val purchase = purchase(total = BigDecimal("100.00"), percent = BigDecimal("0"))

            assertThat(purchase.totalWithDiscount).isEqualByComparingTo(BigDecimal("100.00"))
        }

        @Test
        fun `should be zero when discount is 100 percent`() {
            val purchase = purchase(total = BigDecimal("100.00"), percent = BigDecimal("100"))

            assertThat(purchase.totalWithDiscount).isEqualByComparingTo(BigDecimal("0.00"))
        }

        @Test
        fun `should return total when there is no voucher`() {
            val purchase = purchase(total = BigDecimal("123.45"), percent = null)

            assertThat(purchase.totalWithDiscount).isEqualByComparingTo(BigDecimal("123.45"))
        }
    }

    private fun purchase(total: BigDecimal, percent: BigDecimal?): Purchase {
        val country = Country(name = "Brazil", id = UUID.randomUUID())
        val state = State(name = "Rio de Janeiro", country = country, id = UUID.randomUUID())
        val voucher = percent?.let { Voucher(code = "V-${'$'}it", discount = it, expirationDate = LocalDate.now().plusDays(10)) }

        return Purchase(
            email = "buyer@test.com",
            firstName = "First",
            lastName = "Last",
            document = "08625390056",
            address = "Address, 123",
            complement = "Apt 10",
            city = "City",
            country = country,
            state = state,
            phone = "+5500000000000",
            zipcode = "00000-000",
            voucher = voucher,
            shoppingCart = Purchase.ShoppingCart(total = total, items = listOf()),
            id = UUID.randomUUID()
        )
    }

    @Nested
    inner class HasVoucher {
        @Test
        fun `should return true when voucher is present`() {
            val purchase = purchase()

            assertThat(purchase.hasVoucher()).isTrue()
        }

        @Test
        fun `should return false when voucher is not present`() {
            val purchaseWithNullVoucher = purchase().copy(voucher = null)
            assertThat(purchaseWithNullVoucher.hasVoucher()).isFalse()
        }
    }

    private fun purchase(percent: BigDecimal = BigDecimal.TEN): Purchase {
        val country = Country(name = "Brazil", id = UUID.randomUUID())
        val state = State(name = "Rio de Janeiro", country = country, id = UUID.randomUUID())
        val voucher = Voucher(code = "V-$percent", discount = percent, expirationDate = LocalDate.now().plusDays(10))

        return Purchase(
            email = "buyer@test.com",
            firstName = "First",
            lastName = "Last",
            document = "08625390056",
            address = "Address, 123",
            complement = "Apt 10",
            city = "City",
            country = country,
            state = state,
            phone = "+5500000000000",
            zipcode = "00000-000",
            voucher = voucher,
            shoppingCart = Purchase.ShoppingCart(total = BigDecimal("100.00"), items = listOf()),
            id = UUID.randomUUID()
        )
    }
}
