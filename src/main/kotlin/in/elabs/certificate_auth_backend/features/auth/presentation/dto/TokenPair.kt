package `in`.elabs.certificate_auth_backend.features.auth.presentation.dto

data class TokenPair(
    val accessToken: String,
    val refreshToken: String
)
