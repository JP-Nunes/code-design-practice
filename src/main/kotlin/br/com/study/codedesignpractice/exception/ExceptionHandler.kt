package br.com.study.codedesignpractice.exception

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ValidationErrorResponse> {
        val validationErrorResponse = ValidationErrorResponse(
            invalidProperties = ex.bindingResult.fieldErrors.map { it.field },
            errorMessages = ex.bindingResult.fieldErrors.map { it.defaultMessage }
        )

        return ResponseEntity.badRequest().body(validationErrorResponse)
    }
}

data class ValidationErrorResponse(
    val invalidProperties: List<String>,
    val errorMessages: List<String?>
)