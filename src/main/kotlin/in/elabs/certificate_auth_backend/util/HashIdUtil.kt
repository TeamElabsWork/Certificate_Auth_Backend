package `in`.elabs.certificate_auth_backend.util

import org.hashids.Hashids
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class HashIdUtil(
    @Value("\${HASH_SALT}") private val salt: String
) {
    private val hashids = Hashids(salt, 10)

    fun encode(id: Long): String = hashids.encode(id)

    fun decode(token: String): LongArray = hashids.decode(token)
}