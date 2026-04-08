package br.com.study.codedesignpractice.purchase

import br.com.study.codedesignpractice.book.repository.Book
import br.com.study.codedesignpractice.book.repository.findBooksByIds
import br.com.study.codedesignpractice.book.repository.totalPrice
import br.com.study.codedesignpractice.location.country.Country
import br.com.study.codedesignpractice.location.state.State
import br.com.study.codedesignpractice.validator.CpfCnpj
import br.com.study.codedesignpractice.validator.Exists
import br.com.study.codedesignpractice.voucher.Voucher
import br.com.study.codedesignpractice.voucher.findVoucherByCode
import jakarta.persistence.EntityManager
import jakarta.validation.Valid
import jakarta.validation.constraints.*
import java.math.BigDecimal
import java.util.*

data class CreatePurchaseRequest(
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

    @field:NotBlank
    @field:Exists(entityClass = Voucher::class, fieldName = "code")
    val voucherCode: String?,

    @field:NotNull
    @field:Valid
    val shoppingCart: ShoppingCart?,
) {

    fun toEntity(entityManager: EntityManager): Purchase {
        val shoppingCart = requireNotNull(this.shoppingCart) { "A payment needs a shopping cart" }
        val voucherCode = requireNotNull(this.voucherCode) { "A voucher code is required" }
        val voucher = entityManager.findVoucherByCode(voucherCode) ?: throw IllegalArgumentException("Voucher not found for code $voucherCode")

        return Purchase(
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
            voucher = voucher,
            shoppingCart = shoppingCart.toEntity(entityManager)
        )
    }

     data class ShoppingCart(
         @field:NotNull
         @field:DecimalMin(value = "0.01", inclusive = true)
         val total: BigDecimal?,

         @field:NotEmpty
         @field:NotNull
         @field:Valid
         val items: List<Item>?,
     ) {
         fun toEntity(entityManager: EntityManager): Purchase.ShoppingCart {
             val books: List<Book> = this.items.findBooks(entityManager)

             return Purchase.ShoppingCart(
                 total = books.totalPrice(),
                 items = this.items.toEntity(books)
             )
         }

         private fun List<Item>?.toEntity(books: List<Book>): List<Purchase.ShoppingCart.Item> = this?.map { itemRequest ->
             itemRequest.toEntity(books.find { it.id == itemRequest.id })
         } ?: throw IllegalArgumentException("Shopping cart needs items")

         private fun List<Item>?.findBooks(entityManager: EntityManager): List<Book> = this?.let { items ->
             val booksIds = items.map {
                 require(it.id != null) { "Book id should not be null" }
                 it.id
             }
             entityManager.findBooksByIds(booksIds)
         } ?: throw IllegalArgumentException("Shopping cart needs items")

         data class Item(
             @field:NotNull
             @field:Exists(entityClass = Book::class, fieldName = "id")
             val id: UUID?,

             @field:NotNull
             @field:Min(value = 1)
             val quantity: Int?
         ) {
             fun toEntity(book: Book?) = Purchase.ShoppingCart.Item(
                 book = book,
                 quantity = this.quantity
             )
         }
     }
}
