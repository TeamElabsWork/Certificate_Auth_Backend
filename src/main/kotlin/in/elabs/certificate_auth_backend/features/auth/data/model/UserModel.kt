package `in`.elabs.certificate_auth_backend.features.auth.data.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class UserModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    val name: String,
    val email: String,
    val hashedPassword: String,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "organisation_id", referencedColumnName = "id")
    val organisation: OrganisationModel
)