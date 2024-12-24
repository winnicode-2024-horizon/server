package id.winnicode.horizon.model

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class UpdateUserRequest(

    @field:Size(max = 100)
    @JsonProperty("first_name")
    val firstName: String? = null,

    @field:Size(max = 100)
    @JsonProperty("last_name")
    val lastName: String? = null,

    @field:NotBlank
    @field:Size(max = 100)
    @JsonProperty("current_password")
    val currentPassword: String,

    @field:Size(max = 100)
    @JsonProperty("new_password")
    val newPassword: String? = null,
)
