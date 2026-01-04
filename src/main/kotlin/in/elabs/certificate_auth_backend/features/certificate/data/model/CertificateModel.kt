package `in`.elabs.certificate_auth_backend.features.certificate.data.model

import `in`.elabs.certificate_auth_backend.features.auth.data.model.UserModel
import jakarta.persistence.*
import java.time.Instant

@Entity
data class CertificateModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    val name: String,
    val teamName: String? = null,
    @Column(nullable = false, unique = true)
    val rollNumber: Long,
    @Column(nullable = false)
    val eventName: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    val issuer: UserModel,
    @Column(nullable = false)
    val issuedAt: Instant = Instant.now(),
)