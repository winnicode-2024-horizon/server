package id.winnicode.horizon.service

import id.winnicode.horizon.data.entity.UserToken
import id.winnicode.horizon.data.repository.UserRepository
import id.winnicode.horizon.data.repository.UserTokenRepository
import id.winnicode.horizon.model.LoginUserRequest
import id.winnicode.horizon.model.LoginUserResponse
import id.winnicode.horizon.util.JwtHelper
import id.winnicode.horizon.util.RequestValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.time.Instant
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.util.*

@Service
class AuthService @Autowired constructor(
    private val requestValidator: RequestValidator,
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository,
    private val userTokenRepository: UserTokenRepository,
    private val jwtHelper: JwtHelper,
) {

    @Transactional
    fun login(request: LoginUserRequest): LoginUserResponse {
        requestValidator.validate(request)

        val user = userRepository.findFirstByEmail(request.email)
            .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect email or password") }

        return if (BCrypt.checkpw(request.password, user.password)) {

            val authentication = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken(request.email, request.password)
            )

            val token = jwtHelper.createToken(authentication.name)
            val userToken = UserToken()
                .apply {
                    this.id = "${UUID.randomUUID()}"
                    this.token = token
                    this.user = user
                }
            userTokenRepository.save(userToken)

            LoginUserResponse(
                token = token,
                expiredAt = Instant.now().with(ChronoField.NANO_OF_SECOND, 0).plus(jwtHelper.tokenExpiration),
            )
        } else {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Incorrect email or password")
        }
    }

    @Transactional
    fun logout(userToken: UserToken) {
        userTokenRepository.deleteById(userToken.id)
    }
}


