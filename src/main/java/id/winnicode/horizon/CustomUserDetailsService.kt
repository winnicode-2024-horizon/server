package id.winnicode.horizon

import id.winnicode.horizon.data.entity.User
import id.winnicode.horizon.data.repository.UserRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.springframework.security.core.userdetails.User as UserDetailsUser

@Service
class CustomUserDetailsService @Autowired constructor(
    private val userRepository: UserRepository,
) : UserDetailsService {

    override fun loadUserByUsername(email: String): UserDetails? {
        val user: User = userRepository.findFirstByEmail(email)
            .orElseThrow { UsernameNotFoundException("Username not found") }

        val userDetails = UserDetailsUser.builder()
            .username(user.username)
            .password(user.password)
            .build()

        return userDetails
    }
}