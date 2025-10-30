package `in`.elabs.certificate_auth_backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class CertificateAuthBackendApplication

fun main(args: Array<String>) {
    runApplication<CertificateAuthBackendApplication>(*args)
}