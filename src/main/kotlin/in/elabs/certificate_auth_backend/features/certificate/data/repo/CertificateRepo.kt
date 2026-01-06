package `in`.elabs.certificate_auth_backend.features.certificate.data.repo

import `in`.elabs.certificate_auth_backend.features.certificate.data.model.CertificateModel
import org.springframework.data.jpa.repository.JpaRepository

interface CertificateRepo: JpaRepository<CertificateModel, Long>{
    fun findAllByIssuer_Id(issuerId: Long): List<CertificateModel>
}