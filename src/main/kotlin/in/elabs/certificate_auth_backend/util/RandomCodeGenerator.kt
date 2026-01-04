package `in`.elabs.certificate_auth_backend.util

import org.springframework.stereotype.Component
import java.util.*

@Component
class RandomCodeGenerator {

    fun generateUUIDAuthCode(): String = UUID.randomUUID().toString().replace("-", "").take(12)

}