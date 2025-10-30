package `in`.elabs.certificate_auth_backend.endpoints.certificate

import `in`.elabs.certificate_auth_backend.endpoints.certificate.model.CertificateModel
import org.springframework.stereotype.Service

@Service
class CertificateService {

    private val certificate : MutableList<CertificateModel> = mutableListOf()

    fun getCertificateById(id: Long): CertificateModel? {
        return certificate.find { it.id == id }
    }

    fun createCertificate(request: CertificateModel): Boolean {

        return certificate.add(request)
    }

    fun getAllCertificates(): List<CertificateModel> {
        return certificate.toList()
    }
}