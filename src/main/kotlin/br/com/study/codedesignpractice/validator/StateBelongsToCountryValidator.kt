package br.com.study.codedesignpractice.validator

import br.com.study.codedesignpractice.location.country.Country
import br.com.study.codedesignpractice.location.state.StateRepository
import br.com.study.codedesignpractice.purchase.CreatePurchaseRequest
import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import org.springframework.stereotype.Component
import org.springframework.validation.Errors
import org.springframework.validation.Validator

@Component
class StateBelongsToCountryValidator(
    @field:PersistenceContext private val entityManager: EntityManager,
    private val stateRepository: StateRepository
) : Validator {

    override fun supports(clazz: Class<*>): Boolean {
        return clazz.isAssignableFrom(CreatePurchaseRequest::class.java)
    }

    override fun validate(target: Any, errors: Errors) {
        if (errors.hasErrors()) return

        val createPurchaseRequest = target as CreatePurchaseRequest
        val country = entityManager.find(Country::class.java, createPurchaseRequest.countryId) ?: run {
            errors.rejectValue("countryId", "", "Country not found")
            return
        }

        if (country.hasRegisteredStates() || createPurchaseRequest.stateId != null) {
            country.states().find { it.id == createPurchaseRequest.stateId } ?: run {
                errors.rejectValue("stateId", "", "State does not belong to country")
                return
            }
        }
    }

    private fun Country.hasRegisteredStates() = this.states().isNotEmpty()

    private fun Country.states() = this.id?.let { stateRepository.findByCountryId(it) } ?: emptyList()
}