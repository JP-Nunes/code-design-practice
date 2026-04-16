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

class FindPurchaseResponseTest {

    @Nested
    inner class FromEntity {
        @Test
        fun `should set hasCoupon true and totalWithCouponApplied when voucher exists`() {
            val purchase = purchase(total = bd("200.00"), percent = bd("10"))

            val response = FindPurchaseResponse.fromEntity(purchase)

            assertThat(response.hasCoupon).isTrue()
            assertThat(response.totalWithCouponApplied).isEqualByComparingTo(purchase.totalWithDiscount)
        }

        @Test
        fun `should set hasCoupon false and totalWithCouponApplied null when voucher does not exist`() {
            val purchase = purchase(total = bd("200.00"), percent = null)

            val response = FindPurchaseResponse.fromEntity(purchase)

            assertThat(response.hasCoupon).isFalse()
            assertThat(response.totalWithCouponApplied).isNull()
        }
    }

    private fun bd(s: String) = BigDecimal(s)

    private fun purchase(total: BigDecimal, percent: BigDecimal?): Purchase {
        val country = Country(name = "Brazil", id = UUID.randomUUID())
        val state = State(name = "Rio de Janeiro", country = country, id = UUID.randomUUID())
        val voucher = percent?.let { Voucher(code = "V-$it", discount = it, expirationDate = LocalDate.now().plusDays(10)) }

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
}