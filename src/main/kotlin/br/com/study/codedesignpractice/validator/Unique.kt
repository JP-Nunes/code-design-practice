package br.com.study.codedesignpractice.validator

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import org.springframework.util.Assert
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

@Target(allowedTargets = [AnnotationTarget.PROPERTY_GETTER])
@Retention(value = AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [UniqueValidator::class])
annotation class Unique(
    val message: String = "Value must be unique",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
    val entityClass: KClass<*>,
    val fieldName: String
)

class UniqueValidator() : ConstraintValidator<Unique, String> {

    private lateinit var kClass: KClass<*>
    private lateinit var fieldName: String
    @PersistenceContext private lateinit var entityManager: EntityManager

    override fun initialize(constraintAnnotation: Unique) {
        this.kClass = constraintAnnotation.entityClass
        this.fieldName = constraintAnnotation.fieldName
    }

    override fun isValid(value: String?, context: ConstraintValidatorContext?): Boolean {
        val query = entityManager.createQuery("select 1 from ${kClass.jvmName} where $fieldName=:value")
        query.setParameter("value", value)
        val resultList = query.resultList

        Assert.state(resultList.size <= 1, "More than one ${kClass.jvmName} was found for the given $fieldName")

        return resultList.isEmpty()
    }
}
