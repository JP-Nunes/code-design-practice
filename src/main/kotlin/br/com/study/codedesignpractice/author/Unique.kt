package br.com.study.codedesignpractice.author

import jakarta.validation.Constraint
import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import jakarta.validation.Payload
import org.springframework.beans.factory.annotation.Autowired
import kotlin.reflect.KClass

@Target(allowedTargets = [AnnotationTarget.PROPERTY_GETTER])
@Retention(value = AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [UniqueValidator::class])
annotation class Unique(
    val message: String = "Value must be unique",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)

class UniqueValidator() : ConstraintValidator<Unique, String> {

    @Autowired
    lateinit var authorRepository: AuthorRepository

    override fun isValid(value: String?, context: ConstraintValidatorContext?) : Boolean {
        return value?.let { !authorRepository.existsByEmail(it) } ?: false
    }
}
