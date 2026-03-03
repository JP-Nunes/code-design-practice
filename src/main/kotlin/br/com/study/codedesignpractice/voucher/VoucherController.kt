package br.com.study.codedesignpractice.voucher

import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.net.URI

const val VOUCHERS_V1_PATH = "/v1/payments"

@RestController
@RequestMapping(VOUCHERS_V1_PATH)
class VoucherController(private val voucherRepository: VoucherRepository) {

    @PostMapping
    fun registerVoucher(
        @RequestBody @Valid createVoucherRequest: CreateVoucherRequest
    ): ResponseEntity<CreateVoucherResponse> {
        val voucher = voucherRepository.save(createVoucherRequest.toEntity())
        return ResponseEntity
            .created(URI("$VOUCHERS_V1_PATH/${voucher.id}"))
            .body(voucher.toResponse())
    }
}