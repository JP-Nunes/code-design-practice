package br.com.study.codedesignpractice.voucher

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.data.repository.findByIdOrNull
import java.math.BigDecimal
import java.time.LocalDate

@DataJpaTest
class VoucherRepositoryTest @Autowired constructor(
    val entityManager: TestEntityManager,
    val voucherRepository: VoucherRepository
) {

    @Test
    fun `should be able to register a new voucher`() {
        val voucher = Voucher(
            code = "1234567890",
            discount = BigDecimal(0.1),
            LocalDate.now().plusDays(1)
        )

        entityManager.persistAndFlush(voucher)

        val persistedVoucher = voucherRepository.findByIdOrNull(voucher.id!!)

        assertThat(persistedVoucher).isEqualTo(voucher)
        assertThat(persistedVoucher?.id).isNotNull()
    }
}
