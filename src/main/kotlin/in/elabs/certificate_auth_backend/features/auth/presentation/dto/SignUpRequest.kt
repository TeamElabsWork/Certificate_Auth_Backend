package `in`.elabs.certificate_auth_backend.features.auth.presentation.dto

data class SignUpRequest(
    val signupToken: String,
    val userName: String,
    val userEmail: String,
    val userPassword: String
)
