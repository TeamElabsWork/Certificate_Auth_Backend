package `in`.elabs.certificate_auth_backend.features.admin.data.repo

import `in`.elabs.certificate_auth_backend.features.admin.data.model.AuthCodeModel
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface AuthCodeRepo: JpaRepository<AuthCodeModel, Long>{

    fun existsByCode(code: String): Boolean

    fun deleteByExpiresInBefore(time: Instant): Long
}