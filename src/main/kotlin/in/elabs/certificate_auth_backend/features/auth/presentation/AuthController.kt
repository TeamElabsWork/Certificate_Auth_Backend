package `in`.elabs.certificate_auth_backend.features.auth.presentation

import `in`.elabs.certificate_auth_backend.features.auth.domain.AuthService
import `in`.elabs.certificate_auth_backend.features.auth.presentation.dto.*
import org.springframework.http.HttpStatus
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
        return ResponseEntity.ok(authService.createUser(request,false))
    }
    @PostMapping("/signup/admin")
    fun createAdmin(
        @RequestBody request: SignUpRequest
    ): ResponseEntity<SignUpResponse> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN).build()
        return ResponseEntity.ok(authService.createUser(request,true))
    }

    @PostMapping("/login")
    fun loginUser(
        @RequestBody request: LoginRequest,
    ): ResponseEntity<TokenPair> {
        return ResponseEntity.ok(authService.loginUser(request))
    }

    @PostMapping("/refresh")
    fun refreshToken(
        @RequestBody request: RefreshRequest
    ): ResponseEntity<TokenPair>{
        return ResponseEntity.ok(authService.refresh(request.refreshToken))
    }
}