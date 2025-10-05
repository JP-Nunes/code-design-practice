package br.com.study.codedesignpractice.payment

import br.com.study.codedesignpractice.location.country.Country
import br.com.study.codedesignpractice.location.state.State
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

@DataJpaTest
class PaymentRepositoryTest @Autowired constructor(
    val paymentRepository: PaymentRepository,
    val entityManager: TestEntityManager
) {

    @Test
    fun `should be able to register a new payment`() {
        val country = Country(name = "Brazil")
        val state = State(name = "São Paulo", country = country)
        val payment = Payment(
            email = "user@buyer.com",
            firstName = "Zacarias",
            lastName = "Stoth",
            document = "08625390056",
            address = "Rua das ruas, 1234",
            complement = "Apartemento 200",
            city = "São Paulo",
            country = country,
            state = state,
            phone = "+5511988714077",
            zipcode = "01310-000"
        )

        entityManager.persist(country)
        entityManager.persist(state)
        entityManager.persist(payment)
        entityManager.flush()

        val persistedPayment = paymentRepository.findById(payment.id!!).get()

        assertThat(persistedPayment).isEqualTo(payment)
    }
 }