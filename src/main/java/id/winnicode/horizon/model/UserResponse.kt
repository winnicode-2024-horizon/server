package id.winnicode.horizon.model

import com.fasterxml.jackson.annotation.JsonProperty

data class UserResponse(

    val username: String,

    @JsonProperty("first_name")
    val firstName: String,

    @JsonProperty("last_name")
    val lastName: String,

    val email: String,
)
