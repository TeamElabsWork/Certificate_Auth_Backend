package `in`.elabs.certificate_auth_backend.features.auth.presentation

import `in`.elabs.certificate_auth_backend.features.auth.domain.AuthService
import `in`.elabs.certificate_auth_backend.features.certificate.data.model.CertificateModel
import `in`.elabs.certificate_auth_backend.features.certificate.presentation.dto.CertificateRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping
    fun auth(@RequestBody certificateRequest: CertificateRequest): ResponseEntity<CertificateModel>? {
        return null
    }
}