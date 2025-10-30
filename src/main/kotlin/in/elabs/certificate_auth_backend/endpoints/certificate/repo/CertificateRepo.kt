package `in`.elabs.certificate_auth_backend.endpoints.certificate.repo

import `in`.elabs.certificate_auth_backend.endpoints.certificate.model.CertificateModel
import org.springframework.data.jpa.repository.JpaRepository

interface CertificateRepo: JpaRepository<CertificateModel, Long>