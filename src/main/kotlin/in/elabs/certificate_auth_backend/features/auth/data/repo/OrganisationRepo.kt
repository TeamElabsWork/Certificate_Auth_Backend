package `in`.elabs.certificate_auth_backend.features.auth.data.repo

import `in`.elabs.certificate_auth_backend.features.auth.data.model.OrganisationModel
import org.springframework.data.jpa.repository.JpaRepository

interface OrganisationRepo: JpaRepository<OrganisationModel, Long>