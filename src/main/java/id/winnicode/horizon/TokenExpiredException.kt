package id.winnicode.horizon

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

class TokenExpiredException :
    ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token expired")