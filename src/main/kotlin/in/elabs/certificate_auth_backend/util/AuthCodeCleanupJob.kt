package `in`.elabs.certificate_auth_backend.util

import `in`.elabs.certificate_auth_backend.features.admin.domain.AdminService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import java.time.Instant

@Component
class AuthCodeCleanupJob(
    private val adminService: AdminService
) {
    @Scheduled(cron = "0 0 0 1 * ?")
    fun deleteExpiredAuthCodes() {
        val now = Instant.now()
        val deletedCount = adminService.deleteByExpiresInBefore(now)
        println("🧹 Deleted $deletedCount expired auth codes at $now")
    }
}