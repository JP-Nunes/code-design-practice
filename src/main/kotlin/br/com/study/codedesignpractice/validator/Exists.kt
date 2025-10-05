package br.com.study.codedesignpractice.validator

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import org.springframework.util.Assert
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.reflect.jvm.jvmName

@Target(allowedTargets = [AnnotationTarget.PROPERTY_GETTER, AnnotationTarget.FIELD])
@Retention(value = AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [ExistsValidator::class])
annotation class Exists(
    val message: String = "Value must exist in the database",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = [],
    val entityClass: KClass<*>,
    val fieldName: String
)

class ExistsValidator() : ConstraintValidator<Exists, UUID> {

    private lateinit var kClass: KClass<*>
    private lateinit var fieldName: String
    @PersistenceContext private lateinit var entityManager: EntityManager

    override fun initialize(constraintAnnotation: Exists) {
        this.kClass = constraintAnnotation.entityClass
        this.fieldName = constraintAnnotation.fieldName
    }

    override fun isValid(value: UUID?, context: ConstraintValidatorContext?): Boolean {
        if (value == null) return true // Null values are considered valid, handled by @NotNull if needed

        val query = entityManager.createQuery("select 1 from ${kClass.jvmName} where $fieldName=:value")
        query.setParameter("value", value)
        val resultList = query.resultList

        Assert.state(resultList.size <= 1, "More than one ${kClass.jvmName} was found for the given $fieldName")

        return !resultList.isEmpty()
    }
}
