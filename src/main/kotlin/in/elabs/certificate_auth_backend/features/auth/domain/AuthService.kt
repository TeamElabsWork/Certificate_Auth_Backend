package `in`.elabs.certificate_auth_backend.features.auth.domain

import `in`.elabs.certificate_auth_backend.features.admin.data.repo.AuthCodeRepo
import `in`.elabs.certificate_auth_backend.features.admin.data.repo.OrganisationRepo
import `in`.elabs.certificate_auth_backend.features.auth.data.model.UserModel
import `in`.elabs.certificate_auth_backend.features.auth.data.repo.UserRepo
import `in`.elabs.certificate_auth_backend.features.auth.presentation.dto.LoginRequest
import `in`.elabs.certificate_auth_backend.features.auth.presentation.dto.SignUpRequest
import `in`.elabs.certificate_auth_backend.features.auth.presentation.dto.SignUpResponse
import `in`.elabs.certificate_auth_backend.util.HashPassEncoder
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class AuthService(
    private val userRepo: UserRepo,
    private val organisationRepo: OrganisationRepo,
    private val authCodeRepo: AuthCodeRepo,
    private val hashPassEncoder: HashPassEncoder
) {
    fun validateUserCredentials(email: String, password: String): Boolean {
        // reject blank or malformed input early
        if (email.isBlank() || password.isBlank() || !isValidEmail(email)) return false
        val trimmedEmail = email.trim()
        val user = userRepo.findByEmail(trimmedEmail).firstOrNull() ?: return false
        return hashPassEncoder.matches(password, user.hashedPassword)
    }

    // Simple, reusable email format check using a precompiled regex
    private fun isValidEmail(email: String): Boolean {
        val trimmed = email.trim()
        if (trimmed.isEmpty()) return false
        return EMAIL_REGEX.matches(trimmed)
    }

    companion object {
        private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}")
    }

    @Transactional
    fun createUser(request: SignUpRequest): SignUpResponse {
        val hashedPassword = hashPassEncoder.encodePassword(request.userPassword)?:
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid password")
        val user = userRepo.findByEmail(request.userEmail).firstOrNull()
        if (user != null) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "User already exists")
        }
        val authCode = authCodeRepo.findByCode(request.signupToken) ?:
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Invalid code: ${request.signupToken}")
        val organisation = authCode.organisation
        val newUser = userRepo.save(
            UserModel(
                name = request.name,
                email = request.userEmail,
                hashedPassword = hashedPassword,
                organisation = organisation
            )
        )
        return SignUpResponse(
            name = newUser.name,
            email = newUser.email
        )
    }

    fun loginUser(request: LoginRequest): UserModel {
        val trimmedEmail = request.userEmail.trim()
        if (!isValidEmail(trimmedEmail)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format")
        }
        val user = userRepo.findByEmail(trimmedEmail).firstOrNull() ?:
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        val isPasswordValid = validateUserCredentials(request.userEmail, request.userPassword)
        if (!isPasswordValid) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")
        }
        return user
    }
}