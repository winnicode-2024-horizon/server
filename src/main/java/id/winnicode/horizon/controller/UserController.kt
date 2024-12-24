package id.winnicode.horizon.controller

import id.winnicode.horizon.data.entity.UserToken
import id.winnicode.horizon.model.*
import id.winnicode.horizon.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController @Autowired constructor(
    private val userService: UserService,
) {

    @PostMapping(
        path = ["/api/users/register"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun register(@RequestBody request: RegisterUserRequest): WebResponse<String> {
        userService.register(request)
        return WebResponse<String>()
            .apply {
                status = "${ResponseStatusType.SUCCESS}"
                data = "Ok"
            }
    }

    @PostMapping(
        path = ["/api/users/update"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun update(@RequestBody request: UpdateUserRequest, userToken: UserToken): WebResponse<String> {
        userService.update(request, userToken)
        return WebResponse<String>()
            .apply {
                status = "${ResponseStatusType.SUCCESS}"
                data = "Ok"
            }
    }

    @PostMapping(
        path = ["/api/users/delete"],
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun delete(@RequestBody request: DeleteUserRequest, userToken: UserToken): WebResponse<String> {
        userService.delete(request, userToken)
        return WebResponse<String>()
            .apply {
                status = "${ResponseStatusType.SUCCESS}"
                data = "Ok"
            }
    }

    @GetMapping(
        path = ["/api/users/profile"],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun profile(userToken: UserToken): WebResponse<UserResponse> {
        val response = userService.profile(userToken)
        return WebResponse<UserResponse>()
            .apply {
                status = "${ResponseStatusType.SUCCESS}"
                data = response
            }
    }
}