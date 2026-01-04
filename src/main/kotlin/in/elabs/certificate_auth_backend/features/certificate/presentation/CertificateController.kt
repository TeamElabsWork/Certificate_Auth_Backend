package `in`.elabs.certificate_auth_backend.features.certificate.presentation

import `in`.elabs.certificate_auth_backend.features.certificate.data.model.CertificateModel
import `in`.elabs.certificate_auth_backend.features.certificate.domain.CertificateService
import `in`.elabs.certificate_auth_backend.features.certificate.presentation.dto.CertificateRequest
import `in`.elabs.certificate_auth_backend.features.certificate.presentation.dto.CertificateResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/certificate")
class CertificateController(
    private val certificateService: CertificateService,
) {
    @PostMapping("/create")
    fun createCertificate(@RequestBody request: CertificateRequest): ResponseEntity<String> {
        return ResponseEntity.ok(
            certificateService.createCertificate(request)
        )
    }

    @GetMapping("/verify/{token}")
    fun verifyCertificate(@PathVariable token: String): ResponseEntity<CertificateResponse> {
        val certificate = certificateService.getCertificateResponseById(token) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(certificate)
    }

    @GetMapping
    fun getAllCertificates(): ResponseEntity<List<CertificateModel>> {
        return if (certificateService.getAllCertificates().isEmpty()) {
            ResponseEntity.noContent().build()
        }else{
            ResponseEntity.ok(certificateService.getAllCertificates())
        }
    }
}