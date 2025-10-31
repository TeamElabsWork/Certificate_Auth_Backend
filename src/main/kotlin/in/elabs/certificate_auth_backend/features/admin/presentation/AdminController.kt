package `in`.elabs.certificate_auth_backend.features.admin.presentation

import `in`.elabs.certificate_auth_backend.features.admin.data.model.OrganisationModel
import `in`.elabs.certificate_auth_backend.features.admin.domain.AdminService
import `in`.elabs.certificate_auth_backend.features.admin.presentation.dto.AddOrganisationRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/admin")
class AdminController(
    private val adminService: AdminService
) {
    @PostMapping("/organisations/add")
    fun addOrganisation(
        @RequestBody request: AddOrganisationRequest
    ): ResponseEntity<OrganisationModel> {
        return ResponseEntity.ok(adminService.addOrganisation(request))
    }

    @GetMapping("/authCode/generate/{organisationId}")
    fun generateAuthCode(
        @PathVariable organisationId: Long
    ): ResponseEntity<String> {
        return ResponseEntity.ok(adminService.generateOrganisationAuthCode(organisationId))
    }

    @GetMapping("/organisations")
    fun getOrganisations(): ResponseEntity<List<OrganisationModel?>> {
        return ResponseEntity.ok(adminService.getAllOrganisations())
    }
}