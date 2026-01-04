package `in`.elabs.certificate_auth_backend.features.auth.data.repo

import `in`.elabs.certificate_auth_backend.features.auth.data.model.RefreshTokenModel
import org.springframework.data.jpa.repository.JpaRepository

interface RefreshTokenRepo: JpaRepository<RefreshTokenModel, Long> {
    fun findByUser_IdAndHashedToken(
        userId: Long,
        hashedToken: String
    ): RefreshTokenModel?

    fun deleteByUser_IdAndHashedToken(userId: Long, hashedToken: String)
}