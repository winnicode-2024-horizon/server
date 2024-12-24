package id.winnicode.horizon.data.repository

import id.winnicode.horizon.data.entity.UserToken
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserTokenRepository : JpaRepository<UserToken, String> {

    fun findByToken(token: String): Optional<UserToken>

    fun deleteByToken(token: String): Optional<String>
}