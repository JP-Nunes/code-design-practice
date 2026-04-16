package br.com.study.codedesignpractice.purchase

import br.com.study.codedesignpractice.validator.StateBelongsToCountryValidator
import jakarta.persistence.EntityManager
import jakarta.validation.Valid
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.WebDataBinder
import org.springframework.web.bind.annotation.*
import java.net.URI
import java.util.*

const val PAYMENTS_V1_PATH = "/v1/payments"

@RestController
@RequestMapping(PAYMENTS_V1_PATH)
class PaymentController(
    private val purchaseRepository: PurchaseRepository,
    private val entityManager: EntityManager,
    private val stateBelongsToCountryValidator: StateBelongsToCountryValidator
) {

    @InitBinder
    fun initBinder(binder: WebDataBinder) {
        binder.addValidators(stateBelongsToCountryValidator)
    }

    @PostMapping
    fun registerPayment(
        @RequestBody @Valid createPurchaseRequest: CreatePurchaseRequest
    ): ResponseEntity<CreatePurchaseResponse> {
        val payment = purchaseRepository.save(createPurchaseRequest.toEntity(entityManager))
        return ResponseEntity
            .created(URI("$PAYMENTS_V1_PATH/${payment.id}"))
            .body(CreatePurchaseResponse.fromEntity(payment))
    }

    @GetMapping("/{id}")
    fun findPayment(@PathVariable id: UUID): ResponseEntity<FindPurchaseResponse> {
        val purchase = purchaseRepository.findByIdOrNull(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(FindPurchaseResponse.fromEntity(purchase))
    }
}