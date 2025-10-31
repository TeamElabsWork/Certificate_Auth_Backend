package `in`.elabs.certificate_auth_backend.features.admin.data.model

import jakarta.persistence.*
import java.time.Instant

@Entity
@Table(name = "auth_codes")
data class AuthCodeModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id : Long = 0,
    @Column(unique = true, nullable = false)
    val code: String,
    @Column(nullable = false)
    val expiresIn: Instant,
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val status: AuthCodeStatus = AuthCodeStatus.UNUSED,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", referencedColumnName = "id", nullable = false)
    val organisation: OrganisationModel
)

enum class AuthCodeStatus{
    USED,
    UNUSED
}