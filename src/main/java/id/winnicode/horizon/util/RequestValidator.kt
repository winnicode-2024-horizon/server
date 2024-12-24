package id.winnicode.horizon.util

import jakarta.validation.ConstraintViolationException
import jakarta.validation.Validator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class RequestValidator @Autowired constructor(
    private val validator: Validator,
) {

    fun validate(request: Any) {
        val constraintViolations = validator.validate(request)
        if (constraintViolations.isNotEmpty()) {
            throw ConstraintViolationException(constraintViolations)
        }
    }
}