package `in`.elabs.certificate_auth_backend.features.auth.presentation.dto

data class LoginRequest(
    val userEmail: String,
    val userPassword: String
)
