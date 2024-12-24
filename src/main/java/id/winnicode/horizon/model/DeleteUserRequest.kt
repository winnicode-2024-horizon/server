package id.winnicode.horizon.model

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class DeleteUserRequest(

    @field:NotBlank
    @field:Size(max = 100)
    @field:Email
    val email: String,

    @field:NotBlank
    @field:Size(max = 100)
    val password: String,
)

