package `in`.elabs.certificate_auth_backend.features.auth.data.model

import `in`.elabs.certificate_auth_backend.features.admin.data.model.OrganisationModel
import jakarta.persistence.*

@Entity
@Table(name = "users")
data class UserModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    @Column(nullable = false)
    val name: String,
    @Column(nullable = false, unique = true)
    val email: String,
    @Column(nullable = false)
    val hashedPassword: String,
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", referencedColumnName = "id", nullable = false)
    val organisation: OrganisationModel
)