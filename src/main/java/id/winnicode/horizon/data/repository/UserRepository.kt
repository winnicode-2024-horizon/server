package id.winnicode.horizon.data.repository

import id.winnicode.horizon.data.entity.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, String> {

    fun findFirstByEmail(email: String): Optional<User>
}