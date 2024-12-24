package id.winnicode.horizon.controller.advice

import id.winnicode.horizon.TokenExpiredException
import id.winnicode.horizon.model.ResponseStatusType
import id.winnicode.horizon.model.WebResponse
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class ErrorControllerAdvice {

    @ExceptionHandler(ConstraintViolationException::class)
    fun constraintViolationException(exception: ConstraintViolationException): ResponseEntity<WebResponse<String>> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                WebResponse<String>()
                    .apply {
                        status = "${ResponseStatusType.ERROR}"
                        message = exception.message
                    }
            )

    @ExceptionHandler(ResponseStatusException::class)
    fun responseException(exception: ResponseStatusException): ResponseEntity<WebResponse<String>> =
        ResponseEntity.status(exception.statusCode)
            .body(
                WebResponse<String>()
                    .apply {
                        status = "${ResponseStatusType.FAIL}"
                        message = exception.reason
                    }
            )

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun requestException(exception: HttpMessageNotReadableException): ResponseEntity<WebResponse<String>> =
        ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(
                WebResponse<String>()
                    .apply {
                        status = "${ResponseStatusType.ERROR}"
                        message = exception.message
                    }
            )

    @ExceptionHandler(TokenExpiredException::class)
    fun tokenExpiredException(exception: TokenExpiredException): ResponseEntity<WebResponse<String>> =
        ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(
                WebResponse<String>()
                    .apply {
                        status = "${ResponseStatusType.ERROR}"
                        message = exception.message
                    }
            )
}