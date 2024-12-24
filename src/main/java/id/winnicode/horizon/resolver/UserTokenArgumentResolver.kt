package id.winnicode.horizon.resolver

import id.winnicode.horizon.data.entity.UserToken
import id.winnicode.horizon.data.repository.UserTokenRepository
import id.winnicode.horizon.util.JwtHelper
import jakarta.servlet.http.HttpServletRequest
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.MethodParameter
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.server.ResponseStatusException

@Component
class UserTokenArgumentResolver @Autowired constructor(
    private val jwtHelper: JwtHelper,
    private val userTokenRepository: UserTokenRepository,
) : HandlerMethodArgumentResolver {

    override fun supportsParameter(parameter: MethodParameter): Boolean =
        UserToken::class.java == parameter.parameterType

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?
    ): Any {
        val request = webRequest.nativeRequest as HttpServletRequest
        return jwtHelper.resolveToken(request)
            ?.let { token ->
                userTokenRepository.findByToken(token)
                    .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized") }
            } ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Unauthorized")
    }
}