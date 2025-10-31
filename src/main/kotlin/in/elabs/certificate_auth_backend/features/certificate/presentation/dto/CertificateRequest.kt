package `in`.elabs.certificate_auth_backend.features.certificate.presentation.dto

data class CertificateRequest(
    val name: String,
    val teamName: String?,
    val rollNumber: Long,
    val eventName: String,
    val issuerId: Long,
    val issuedAt: String
)
