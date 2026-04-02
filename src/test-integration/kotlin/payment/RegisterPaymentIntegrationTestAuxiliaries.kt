package payment

import br.com.study.codedesignpractice.author.Author
import br.com.study.codedesignpractice.book.repository.Book
import br.com.study.codedesignpractice.category.Category
import br.com.study.codedesignpractice.purchase.CreatePurchaseRequest
import br.com.study.codedesignpractice.voucher.Voucher
import java.math.BigDecimal
import java.time.LocalDate
import java.util.*

const val PAYMENTS_V1_PATH = "/v1/payments"

fun createPaymentRequest(
    email: String? = "test@example.com",
    firstName: String? = "John",
    lastName: String? = "Doe",
    document: String? = "793.222.040-87",
    address: String? = "Av. Paulista, 1000",
    complement: String? = "Ap 101",
    city: String? = "São Paulo",
    countryId: UUID?,
    stateId: UUID?,
    zipcode: String? = "01310-000",
    phone: String? = "+5511999999999",
    voucherCode: String? = "VOUCHER40",
    shoppingCart: CreatePurchaseRequest.ShoppingCart? = defaultShoppingCart()
) = CreatePurchaseRequest(
    email = email,
    firstName = firstName,
    lastName = lastName,
    document = document,
    address = address,
    complement = complement,
    city = city,
    countryId = countryId,
    stateId = stateId,
    zipcode = zipcode,
    phone = phone,
    voucherCode = voucherCode,
    shoppingCart = shoppingCart
)

private fun defaultShoppingCart() = CreatePurchaseRequest.ShoppingCart(
    total = BigDecimal(100),
    items = listOf(CreatePurchaseRequest.ShoppingCart.Item(UUID.randomUUID(), quantity = 2))
)

fun book(persistedCategory: Category, persistedAuthor: Author) = Book(
    title = "Fundamentals of Software Architecture: An Engineering Approach",
    summary = "A book about Software Architecture.",
    tableOfContents = "Table of Contents",
    price = 20.toBigDecimal(),
    numberOfPages = 300,
    isbn = "123-456-789",
    publishDate = LocalDate.now().plusDays(10),
    category = persistedCategory,
    author = persistedAuthor
)

fun voucher(
    code: String = "VOUCHER40",
    discount: BigDecimal = BigDecimal.TEN,
    expirationDate: LocalDate = LocalDate.now().plusDays(30)
) = Voucher(code, discount, expirationDate)