package br.com.study.codedesignpractice.payment

import br.com.study.codedesignpractice.book.repository.Book
import br.com.study.codedesignpractice.location.country.Country
import br.com.study.codedesignpractice.location.state.State
import br.com.study.codedesignpractice.validator.Exists
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.ManyToOne
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.AssertTrue
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotEmpty
import java.math.BigDecimal
import java.util.UUID
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
    @field:Pattern(regexp = "^\\d{11}$|^\\d{14}$", message = "document must be a CPF (11 digits) or CNPJ (14 digits)")
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

//    @field:NotNull
//    @field:Valid
//    val shoppingCart: ShoppingCart,

    @Id
    @GeneratedValue
    val id: UUID? = null,
) {

//    data class ShoppingCart(
//        @field:NotNull
//        @field:DecimalMin(value = "0.01", inclusive = true)
//        val total: BigDecimal?,
//
//        @field:NotEmpty
//        @field:Valid
//        val items: List<Item>?,
//    ) {
//
//        data class Item(
//            @field:NotNull
//            val book: Book?,
//
//            @field:NotNull
//            @field:Min(value = 1)
//            val quantity: Int?
//        )
//    }
}
