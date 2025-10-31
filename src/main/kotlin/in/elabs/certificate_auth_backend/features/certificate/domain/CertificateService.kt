package `in`.elabs.certificate_auth_backend.features.certificate.domain

import `in`.elabs.certificate_auth_backend.features.auth.data.repo.UserRepo
import `in`.elabs.certificate_auth_backend.features.certificate.data.model.CertificateModel
import `in`.elabs.certificate_auth_backend.features.certificate.data.repo.CertificateRepo
import `in`.elabs.certificate_auth_backend.features.certificate.presentation.dto.CertificateRequest
import `in`.elabs.certificate_auth_backend.features.certificate.presentation.dto.CertificateResponse
import org.springframework.stereotype.Service

@Service
class CertificateService(
    private val certificateRepo: CertificateRepo,
    private val userRepo: UserRepo
) {

    fun getCertificateResponseById(id: Long): CertificateResponse? {
        val certificate = certificateRepo.findById(id).orElse(null) ?: return null

        return CertificateResponse(
            id = certificate.id,
            name = certificate.name,
            teamName = certificate.teamName,
            rollNumber = certificate.rollNumber,
            eventName = certificate.eventName,
            issuedAt = certificate.issuedAt,
            organisationName = certificate.issuer?.organisation?.name
        )
    }


    fun createCertificate(request: CertificateRequest): CertificateModel {
        val issuer = request.issuerId?.let { userRepo.findById(it).orElse(null) }

        val certificate = CertificateModel(
            id = 0, // auto-generated
            name = request.name,
            teamName = request.teamName,
            rollNumber = request.rollNumber,
            eventName = request.eventName,
            issuer = issuer,
            issuedAt = request.issuedAt
        )

        return certificateRepo.save(certificate)
    }

    fun getAllCertificates(): List<CertificateModel> {
        return certificateRepo.findAll()
    }
}