package id.winnicode.horizon.service

import id.winnicode.horizon.data.entity.User
import id.winnicode.horizon.data.entity.UserToken
import id.winnicode.horizon.data.repository.UserRepository
import id.winnicode.horizon.model.DeleteUserRequest
import id.winnicode.horizon.model.RegisterUserRequest
import id.winnicode.horizon.model.UpdateUserRequest
import id.winnicode.horizon.model.UserResponse
import id.winnicode.horizon.util.RequestValidator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCrypt
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class UserService @Autowired constructor(
    private val userRepository: UserRepository,
    private val requestValidator: RequestValidator,
) {

    @Transactional
    fun register(request: RegisterUserRequest) {
        requestValidator.validate(request)

        if (userRepository.existsById(request.username)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Username already registered")
        }

        val user = User()
        user.apply {
            username = request.username
            firstName = request.firstName
            lastName = request.lastName
            email = request.email
            password = BCrypt.hashpw(request.password, BCrypt.gensalt())
        }
        userRepository.save(user)
    }

    @Transactional
    fun update(request: UpdateUserRequest, userToken: UserToken) {
        requestValidator.validate(request)

        val user = userToken.user
        if (BCrypt.checkpw(request.currentPassword, user.password)) {
            if (request.firstName != null) {
                user.firstName = request.firstName
            }

            if (request.lastName != null) {
                user.lastName = request.lastName
            }

            if (request.newPassword != null) {
                user.password = BCrypt.hashpw(request.newPassword, BCrypt.gensalt())
            }

            userRepository.save(user)
        } else {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Password incorrect")
        }
    }

    @Transactional
    fun delete(request: DeleteUserRequest, userToken: UserToken) {
        requestValidator.validate(request)

        val user = userToken.user
        if (BCrypt.checkpw(request.password, user.password)) {
            userRepository.delete(user)
        } else {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Password incorrect")
        }
    }

    @Transactional(readOnly = true)
    fun profile(userToken: UserToken): UserResponse {
        val user = userToken.user
        return UserResponse(
            username = user.username,
            firstName = user.firstName,
            lastName = user.lastName,
            email = user.email,
        )
    }
}