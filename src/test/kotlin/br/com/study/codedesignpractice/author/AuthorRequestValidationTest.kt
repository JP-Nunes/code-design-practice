package br.com.study.codedesignpractice.author

import jakarta.validation.Validation
import jakarta.validation.Validator
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class AuthorRequestValidationTest {

    lateinit var validator: Validator

    @BeforeEach fun setup() {
        validator = Validation.buildDefaultValidatorFactory().validator
    }

    @Test
    fun `should fail when name is blank`() {
        val request = AuthorRequest(name = "", email = "test@example.com", description = "desc")
        val violations = validator.validate(request)
        assertThat(violations).anyMatch { it.propertyPath.toString() == "name" }
    }

    @Test
    fun `should fail when email is invalid`() {
        val request = AuthorRequest(name = "Name", email = "invalid", description = "desc")
        val violations = validator.validate(request)
        assertThat(violations).anyMatch { it.propertyPath.toString() == "email" }
    }

    @Test
    fun `should fail when description is too long`() {
        val wayTooLongDescription = "a".repeat(401)
        val request = AuthorRequest(name = "Name", email = "test@example.com", description = wayTooLongDescription)
        val violations = validator.validate(request)
        assertThat(violations).anyMatch { it.propertyPath.toString() == "description" }
    }

    @Test
    fun `should pass with valid data`() {
        val request = AuthorRequest(name = "Name", email = "test@example.com", description = "desc")
        val violations = validator.validate(request)
        assertThat(violations).isEmpty()
    }
}