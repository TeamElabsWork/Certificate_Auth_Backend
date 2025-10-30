package `in`.elabs.certificate_auth_backend.endpoints.certificate

import `in`.elabs.certificate_auth_backend.endpoints.certificate.model.CertificateModel
import `in`.elabs.certificate_auth_backend.util.HashIdUtil
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/certificate")
class CertificateController(
    private val certificateService: CertificateService,
    private val hashIdUtil: HashIdUtil
) {
    @PostMapping("/create")
    fun createCertificate(@RequestBody request: CertificateModel): ResponseEntity<String> {
        val savedCertificate = certificateService.createCertificate(request)

        if (!savedCertificate){
            return ResponseEntity.badRequest().body("Error: certificate cannot be created")
        }
        val token = hashIdUtil.encode(request.id)

        return ResponseEntity.ok(token)
    }

    @GetMapping("/verify/{token}")
    fun verifyCertificate(@PathVariable token: String): ResponseEntity<CertificateModel> {
        val decoded = hashIdUtil.decode(token)

        if (decoded.isEmpty()) {
            return ResponseEntity.badRequest().build()
        }

        val realId = decoded[0]

        val certificate = certificateService.getCertificateById(realId) ?: return ResponseEntity.notFound().build()

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