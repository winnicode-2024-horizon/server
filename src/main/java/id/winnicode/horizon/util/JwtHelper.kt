package id.winnicode.horizon.util

import id.winnicode.horizon.TokenExpiredException
import id.winnicode.horizon.data.repository.UserTokenRepository
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.*
import javax.crypto.SecretKey

@Component
class JwtHelper @Autowired constructor(
    private val userTokenRepository: UserTokenRepository
) {

    private val key: SecretKey = Jwts.SIG.HS256.key().build()
    val tokenExpiration: Duration = Duration.of(7L, ChronoUnit.DAYS)

    private val jwtParser = Jwts.parser()
        .verifyWith(key)
        .build()

    fun createToken(username: String): String =
        Jwts.builder()
            .subject(username)
            .apply {
                issuedAt(Date.from(Instant.now().with(ChronoField.NANO_OF_SECOND, 0)))
                expiration(Date.from(Instant.now().with(ChronoField.NANO_OF_SECOND, 0).plus(tokenExpiration)))
                signWith(key)
            }
            .compact()

    fun resolveToken(request: HttpServletRequest): String? {
        return request.getHeader(AUTHORIZATION_HEADER)
            .takeIf { auth ->
                auth != null && auth.startsWith(AUTHORIZATION_TOKEN_PREFIX)
            }
            ?.substring(
                AUTHORIZATION_TOKEN_PREFIX.length + 1
            )
    }

    fun parseToken(token: String): Claims {
        val claims: Claims? = jwtParser
            .parseSignedClaims(token)
            .payload
            .takeIf { claims ->
                val claimsIssuedAt = claims.issuedAt.toInstant()
                claimsIssuedAt.isBefore(claimsIssuedAt.plus(tokenExpiration))
            }

        return if (claims != null) {
            claims
        } else {
            userTokenRepository.deleteByToken(token)
            throw TokenExpiredException()
        }

    }
}

private const val AUTHORIZATION_HEADER = "Authorization"
private const val AUTHORIZATION_TOKEN_PREFIX = "Bearer"