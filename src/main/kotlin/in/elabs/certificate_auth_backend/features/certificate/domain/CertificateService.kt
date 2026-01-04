package `in`.elabs.certificate_auth_backend.features.certificate.domain

import `in`.elabs.certificate_auth_backend.features.auth.data.repo.UserRepo
import `in`.elabs.certificate_auth_backend.features.certificate.data.model.CertificateModel
import `in`.elabs.certificate_auth_backend.features.certificate.data.repo.CertificateRepo
import `in`.elabs.certificate_auth_backend.features.certificate.presentation.dto.CertificateRequest
import `in`.elabs.certificate_auth_backend.features.certificate.presentation.dto.CertificateResponse
import `in`.elabs.certificate_auth_backend.util.HashIdUtil
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class CertificateService(
    private val certificateRepo: CertificateRepo,
    private val userRepo: UserRepo,
    private val hashIdUtil: HashIdUtil
) {

    fun getCertificateResponseById(token: String): CertificateResponse? {

        val decoded = hashIdUtil.decode(token)

        if (decoded.isEmpty()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid token: $token")
        }

        val realId = decoded[0]

        val certificate = certificateRepo.findById(realId).orElse(null) ?: return null

        return CertificateResponse(
            id = certificate.id,
            name = certificate.name,
            teamName = certificate.teamName,
            rollNumber = certificate.rollNumber,
            eventName = certificate.eventName,
            issuedAt = certificate.issuedAt,
            organisationName = certificate.issuer.organisation.name
        )
    }

    @Transactional
    fun createCertificate(request: CertificateRequest): String {
        val issuerId = SecurityContextHolder.getContext()
            .authentication
            ?.principal as? Long
            ?: throw ResponseStatusException(
                HttpStatus.UNAUTHORIZED,
                "Unauthenticated"
            )

        val issuer = userRepo.findById(issuerId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        }

        val certificate = certificateRepo.save(
            CertificateModel(
                name = request.name,
                teamName = request.teamName,
                rollNumber = request.rollNumber,
                eventName = request.eventName,
                issuer = issuer,
            )
        )

        return hashIdUtil.encode(certificate.id)
    }

    fun getAllCertificates(): List<CertificateModel> {
        return certificateRepo.findAll()
    }
}