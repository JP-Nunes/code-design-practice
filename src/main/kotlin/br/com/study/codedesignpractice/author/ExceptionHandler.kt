package br.com.study.codedesignpractice.author

import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class ExceptionHandler {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ValidationErrorResponse {
        return ValidationErrorResponse(
            invalidProperties = ex.bindingResult.fieldErrors.map { it.field },
            errorMessages = ex.bindingResult.fieldErrors.map { it.defaultMessage }
        )
    }
}

data class ValidationErrorResponse(
    val invalidProperties: List<String>,
    val errorMessages: List<String?>
)