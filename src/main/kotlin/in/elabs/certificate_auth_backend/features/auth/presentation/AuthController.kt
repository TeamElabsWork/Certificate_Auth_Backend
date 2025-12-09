package `in`.elabs.certificate_auth_backend.features.auth.presentation

import `in`.elabs.certificate_auth_backend.features.auth.data.model.UserModel
import `in`.elabs.certificate_auth_backend.features.auth.domain.AuthService
import `in`.elabs.certificate_auth_backend.features.auth.presentation.dto.LoginRequest
import `in`.elabs.certificate_auth_backend.features.auth.presentation.dto.SignUpRequest
import `in`.elabs.certificate_auth_backend.features.auth.presentation.dto.SignUpResponse
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

    @PostMapping("/signup")
    fun createUser(
        @RequestBody request: SignUpRequest
    ): ResponseEntity<SignUpResponse> {
        return ResponseEntity.ok(authService.createUser(request))
    }

    @PostMapping("/login")
    fun loginUser(
        @RequestBody request: LoginRequest
    ): ResponseEntity<UserModel> {
        return ResponseEntity.ok(authService.loginUser(request))
    }
}