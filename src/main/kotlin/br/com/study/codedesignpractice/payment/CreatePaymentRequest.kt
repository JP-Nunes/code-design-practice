package br.com.study.codedesignpractice.payment

import br.com.study.codedesignpractice.location.country.Country
import br.com.study.codedesignpractice.location.state.State
import br.com.study.codedesignpractice.book.repository.Book
import br.com.study.codedesignpractice.validator.CpfCnpj
import br.com.study.codedesignpractice.validator.Exists
import jakarta.persistence.EntityManager
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.util.*

 data class CreatePaymentRequest(
    @field:NotBlank
    @field:Email
    val email: String?,

    @field:NotBlank
    val firstName: String?,

    @field:NotBlank
    val lastName: String?,

    @field:NotBlank
    @field:CpfCnpj
    val document: String?,

    @field:NotBlank
    val address: String?,

    @field:NotBlank
    val complement: String?,

    @field:NotBlank
    val city: String?,

    @field:NotNull
    @field:Exists(entityClass = Country::class, fieldName = "id")
    val countryId: UUID?,

    @field:Exists(entityClass = State::class, fieldName = "id")
    val stateId: UUID?,

    @field:NotBlank
    val zipcode: String?,

    @field:NotBlank
    val phone: String?,

    @field:NotNull
    @field:Valid
    val shoppingCart: ShoppingCart,
) {

    fun toEntity(entityManager: EntityManager): Payment {
        val country = requireNotNull(entityManager.find(Country::class.java, this.countryId)) { "Country not found" }
        val state = stateId?.let { entityManager.find(State::class.java, this.stateId) }

        return Payment(
            email = this.email,
            firstName = this.firstName,
            lastName = this.lastName,
            document = this.document,
            address = this.address,
            complement = this.complement,
            city = this.city,
            country = country,
            state = state,
            phone = this.phone,
            zipcode = this.zipcode,
            shoppingCart = Payment.ShoppingCart(
                total = this.shoppingCart.total,
                items = this.shoppingCart.items?.map {
                    val book = requireNotNull(entityManager.find(Book::class.java, it.bookId)) { "Book not found" }
                    Payment.ShoppingCart.Item(book, it.quantity)
                }
            )
        )
    }

     data class ShoppingCart(
         @field:NotNull
         @field:DecimalMin(value = "0.01", inclusive = true)
         val total: BigDecimal?,

         @field:NotEmpty
         @field:Valid
         val items: List<Item>?,
     ) {

         data class Item(
             @field:NotNull
             @field:Exists(entityClass = Book::class, fieldName = "id")
             val bookId: UUID?,

             @field:NotNull
             @field:Min(value = 1)
             val quantity: Int?
         )
     }
}
