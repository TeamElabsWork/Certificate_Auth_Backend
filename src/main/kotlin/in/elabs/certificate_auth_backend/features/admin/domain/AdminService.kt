package `in`.elabs.certificate_auth_backend.features.admin.domain

import `in`.elabs.certificate_auth_backend.features.admin.data.model.AuthCodeModel
import `in`.elabs.certificate_auth_backend.features.admin.data.model.OrganisationModel
import `in`.elabs.certificate_auth_backend.features.admin.data.repo.AuthCodeRepo
import `in`.elabs.certificate_auth_backend.features.admin.data.repo.OrganisationRepo
import `in`.elabs.certificate_auth_backend.features.admin.presentation.dto.AddOrganisationRequest
import `in`.elabs.certificate_auth_backend.util.RandomCodeGenerator
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.time.ZoneOffset
import java.time.ZonedDateTime

@Service
class AdminService(
    private val authCodeRepo: AuthCodeRepo,
    private val organisationRepo: OrganisationRepo,
    private val randomCodeGenerator: RandomCodeGenerator
) {

    fun getAllOrganisations() = organisationRepo.findAll()

    @Transactional
    fun generateOrganisationAuthCode(organisationId: Long): String {
        val organisation = organisationRepo.findById(organisationId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND, "Organisation not found")
        }

        var authCode: String
        do {
            authCode = randomCodeGenerator.generateUUIDAuthCode()
        } while (authCodeRepo.existsByCode(authCode))

        authCodeRepo.save(
            AuthCodeModel(
                code = authCode,
                expiresIn = ZonedDateTime.now(ZoneOffset.UTC).plusMonths(1).toInstant(),
                organisation = organisation
            )
        )

        return authCode
    }

    @Transactional
    fun addOrganisation(request: AddOrganisationRequest): OrganisationModel {
        val organisation = organisationRepo.findByName(request.organisationName)
        if (organisation != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Organisation already exists")
        }
        return organisationRepo.save(
            OrganisationModel(
                name = request.organisationName,
            )
        )
    }
}