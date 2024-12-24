package id.winnicode.horizon

import id.winnicode.horizon.data.repository.UserTokenRepository
import id.winnicode.horizon.util.JwtHelper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthorizationFilter @Autowired constructor(
    private val jwtHelper: JwtHelper,
    private val userTokenRepository: UserTokenRepository,
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val token = jwtHelper.resolveToken(request)
        return if (token != null && userTokenRepository.findByToken(token).isPresent) {
            val claims = jwtHelper.parseToken(token)
            SecurityContextHolder.getContext().authentication =
                UsernamePasswordAuthenticationToken(
                    claims.subject,
                    null,
                    emptyList()
                )
            filterChain.doFilter(request, response)
        } else {
            filterChain.doFilter(request, response)
        }
    }
}