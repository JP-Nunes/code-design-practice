package br.com.study.codedesignpractice.payment

import jakarta.persistence.EntityManager
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

const val PAYMENTS_V1_PATH = "/v1/payments"

@RestController
@RequestMapping(PAYMENTS_V1_PATH)
class PaymentController(
    private val paymentRepository: PaymentRepository,
    private val entityManager: EntityManager
) {

    @PostMapping
    fun registerPayment(
        @RequestBody @Valid createPaymentRequest: CreatePaymentRequest
    ): ResponseEntity<CreatePaymentResponse> {
        val payment = paymentRepository.save(createPaymentRequest.toEntity(entityManager))
        return ResponseEntity
            .created(URI("$PAYMENTS_V1_PATH/${payment.id}"))
            .body(CreatePaymentResponse.fromEntity(payment))
    }
}