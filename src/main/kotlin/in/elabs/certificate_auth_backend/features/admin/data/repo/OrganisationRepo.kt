package `in`.elabs.certificate_auth_backend.features.admin.data.repo

import `in`.elabs.certificate_auth_backend.features.admin.data.model.OrganisationModel
import org.springframework.data.jpa.repository.JpaRepository

interface OrganisationRepo: JpaRepository<OrganisationModel, Long>{
    fun findByName(name: String): OrganisationModel?
}