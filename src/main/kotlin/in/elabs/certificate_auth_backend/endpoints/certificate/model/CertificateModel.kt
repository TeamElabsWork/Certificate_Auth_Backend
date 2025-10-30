package `in`.elabs.certificate_auth_backend.endpoints.certificate.model

import `in`.elabs.certificate_auth_backend.endpoints.auth.model.UserModel
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne

@Entity
data class CertificateModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    val name: String,
    val teamName: String?,
    val rollNumber: Long,
    val eventName: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    val issuer: UserModel,
    val issuedAt: String,
)