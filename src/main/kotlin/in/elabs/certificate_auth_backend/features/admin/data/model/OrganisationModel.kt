package `in`.elabs.certificate_auth_backend.features.admin.data.model

import jakarta.persistence.*

@Entity
@Table(name = "organisations")
data class OrganisationModel(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,
    val name: String
)