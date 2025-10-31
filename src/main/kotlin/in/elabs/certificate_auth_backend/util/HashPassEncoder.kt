package `in`.elabs.certificate_auth_backend.util

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Component

@Component
class HashPassEncoder {

    private val bcrypt = BCryptPasswordEncoder()

    fun encodePassword(password: String): String? = bcrypt.encode(password)

    fun matches(password: String, hashedPassword: String): Boolean = bcrypt.matches(password, hashedPassword)
}