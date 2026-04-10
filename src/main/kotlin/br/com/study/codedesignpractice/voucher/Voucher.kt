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
import java.math.RoundingMode
import java.time.LocalDate

@Entity
data class Voucher(
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
) {

    /**
     * Multiplicative factor derived from [discount] percentage, using the return of this function to multiply
     * by another value will result in a final value with the percentage of the discount applied.
     *
     * Semantics:
     * - If the discount is 0 -> returns 1.00 (no discount, keeps the original total)
     * - If the discount is 10% -> returns 0.90 (applies ten percent off the total)
     * - If the discount is 100% -> returns 0.00 (free)
     * - >100% -> 0.00 (clamped at zero to avoid negative multiplication)
     */
    val discountMultiplier: BigDecimal
        get() {
            val discount = this.discount ?: throw IllegalStateException("Discount cannot be null")
            require(discount.signum() >= 0) { "Discount cannot be negative: $discount" }

            val proportion = discount.movePointLeft(2)
            val multiplier = BigDecimal.ONE - proportion

            return multiplier
                .coerceIn(BigDecimal.ZERO, BigDecimal.ONE)
                .setScale(2, RoundingMode.HALF_UP)
        }
}
