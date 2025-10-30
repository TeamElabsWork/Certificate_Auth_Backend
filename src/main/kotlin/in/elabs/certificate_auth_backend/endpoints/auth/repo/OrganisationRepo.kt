package `in`.elabs.certificate_auth_backend.endpoints.auth.repo

import `in`.elabs.certificate_auth_backend.endpoints.auth.model.OrganisationModel
import org.springframework.data.jpa.repository.JpaRepository

interface OrganisationRepo: JpaRepository<OrganisationModel, Long>