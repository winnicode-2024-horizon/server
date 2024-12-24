package id.winnicode.horizon.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.time.Instant

data class LoginUserResponse(

    val token: String,

    @JsonProperty("expired_at")
    val expiredAt: Instant,
)
