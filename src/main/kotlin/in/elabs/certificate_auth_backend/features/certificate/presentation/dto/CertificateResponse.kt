package `in`.elabs.certificate_auth_backend.features.certificate.presentation.dto

import java.time.Instant

data class CertificateResponse(
    val id: Long,
    val name: String,
    val teamName: String?,
    val rollNumber: Long,
    val eventName: String,
    val issuedAt: Instant,
    val organisationName: String?
)
