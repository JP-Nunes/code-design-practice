package br.com.study.codedesignpractice.payment

import br.com.study.codedesignpractice.location.country.Country
import br.com.study.codedesignpractice.location.state.State
import br.com.study.codedesignpractice.book.repository.Book
import br.com.study.codedesignpractice.book.repository.findBooksByIds
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
    val shoppingCart: ShoppingCart?,
) {

    fun toEntity(entityManager: EntityManager) = Purchase(
        email = this.email,
        firstName = this.firstName,
        lastName = this.lastName,
        document = this.document,
        address = this.address,
        complement = this.complement,
        city = this.city,
        country = entityManager.find(Country::class.java, this.countryId),
        state = this.stateId?.let { entityManager.find(State::class.java, this.stateId) },
        phone = this.phone,
        zipcode = this.zipcode,
        shoppingCart = this.shoppingCart?.toEntity(entityManager) ?: throw IllegalArgumentException("A payment needs a shopping cart")
    )

     data class ShoppingCart(
         @field:NotNull
         @field:DecimalMin(value = "0.01", inclusive = true)
         val total: BigDecimal?,

         @field:NotEmpty
         @field:Valid
         val items: List<Item>?,
     ) {
         fun toEntity(entityManager: EntityManager): Purchase.ShoppingCart {
             val books: List<Book> = this.items?.let { items ->
                 val booksIds = items.mapNotNull { it.id }
                 entityManager.findBooksByIds(booksIds)
             } ?: emptyList()

             return Purchase.ShoppingCart(
                 total = this.total,
                 items = this.items?.map { bookRequest ->
                     Purchase.ShoppingCart.Item(
                         book = books.find { it.id == bookRequest.id },
                         quantity = bookRequest.quantity
                     )
                 }
             )
         }

         data class Item(
             @field:NotNull
             @field:Exists(entityClass = Book::class, fieldName = "id")
             val id: UUID?,

             @field:NotNull
             @field:Min(value = 1)
             val quantity: Int?
         )
     }
}
