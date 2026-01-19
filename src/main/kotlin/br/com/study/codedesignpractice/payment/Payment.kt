package br.com.study.codedesignpractice.payment

import br.com.study.codedesignpractice.book.repository.Book
import br.com.study.codedesignpractice.location.country.Country
import br.com.study.codedesignpractice.location.state.State
import br.com.study.codedesignpractice.validator.CpfCnpj
import jakarta.persistence.ElementCollection
import jakarta.persistence.Embeddable
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.util.*

@Entity
data class Payment(
    @field:NotBlank
    @field:Email
    val email: String?,

    @field:NotBlank
    val firstName: String?,

    @field:NotBlank
    val lastName: String?,

    @field:NotBlank
    @CpfCnpj
    val document: String?,

    @field:NotBlank
    val address: String?,

    @field:NotBlank
    val complement: String?,

    @field:NotBlank
    val city: String?,

    @field:NotNull
    @field:ManyToOne
    val country: Country?,

    @field:ManyToOne
    val state: State?,

    @field:NotBlank
    val phone: String?,

    @field:NotBlank
    val zipcode: String?,

    @field:NotNull
    @field:Valid
    val shoppingCart: ShoppingCart?,

    @field:Id
    @field:GeneratedValue
    val id: UUID? = null,
) {

    @Embeddable
    data class ShoppingCart(
        @field:NotNull
        @field:DecimalMin(value = "0.01", inclusive = true)
        val total: BigDecimal?,

        @field:NotEmpty
        @field:Valid
        @ElementCollection
        val items: List<Item>?,
    ) {

        @Embeddable
        data class Item(
            @field:NotNull
            @field:ManyToOne
            val book: Book?,

            @field:NotNull
            @field:Min(value = 1)
            val quantity: Int?
        )
    }
}
