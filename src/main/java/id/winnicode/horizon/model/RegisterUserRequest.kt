package id.winnicode.horizon.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterUserRequest(

    @field:NotBlank
    @field:Size(max = 100)
    val username: String,

    @field:NotBlank
    @field:Size(max = 100)
    @JsonProperty("first_name")
    val firstName: String,

    @field:NotBlank
    @field:Size(max = 100)
    @JsonProperty("last_name")
    val lastName: String,

    @field:NotBlank
    @field:Size(max = 100)
    @field:Email(regexp = ".+@.+\\..+")
    val email: String,

    @field:NotBlank
    @field:Size(max = 100)
    val password: String,
)