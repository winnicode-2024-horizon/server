package id.winnicode.horizon.controller

import id.winnicode.horizon.data.entity.UserToken
import id.winnicode.horizon.model.LoginUserRequest
import id.winnicode.horizon.model.LoginUserResponse
import id.winnicode.horizon.model.ResponseStatusType
import id.winnicode.horizon.model.WebResponse
import id.winnicode.horizon.service.AuthService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class AuthController @Autowired constructor(
    private val authService: AuthService,
) {

    @PostMapping(
        path = ["/api/users/login"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun login(@RequestBody request: LoginUserRequest): WebResponse<LoginUserResponse> {
        val response = authService.login(request)

        return WebResponse<LoginUserResponse>()
            .apply {
                status = "${ResponseStatusType.SUCCESS}"
                data = response
            }
    }

    @PostMapping(
        path = ["/api/users/logout"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun logout(userToken: UserToken): WebResponse<String> {
        authService.logout(userToken)
        return WebResponse<String>()
            .apply {
                status = "${ResponseStatusType.SUCCESS}"
                data = "Ok"
            }
    }
}