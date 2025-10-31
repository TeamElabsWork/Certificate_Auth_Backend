package `in`.elabs.certificate_auth_backend.features.auth.domain

import `in`.elabs.certificate_auth_backend.features.admin.data.repo.OrganisationRepo
import `in`.elabs.certificate_auth_backend.features.auth.data.repo.UserRepo
import `in`.elabs.certificate_auth_backend.util.HashPassEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepo: UserRepo,
    private val organisationRepo: OrganisationRepo,
    private val hashPassEncoder: HashPassEncoder
) {

}