package `in`.elabs.certificate_auth_backend.features.auth.domain

import `in`.elabs.certificate_auth_backend.features.admin.data.repo.AuthCodeRepo
import `in`.elabs.certificate_auth_backend.features.admin.data.repo.OrganisationRepo
import `in`.elabs.certificate_auth_backend.features.auth.data.model.RefreshTokenModel
import `in`.elabs.certificate_auth_backend.features.auth.data.model.UserModel
import `in`.elabs.certificate_auth_backend.features.auth.data.repo.RefreshTokenRepo
import `in`.elabs.certificate_auth_backend.features.auth.data.repo.UserRepo
import `in`.elabs.certificate_auth_backend.features.auth.presentation.dto.LoginRequest
import `in`.elabs.certificate_auth_backend.features.auth.presentation.dto.SignUpRequest
import `in`.elabs.certificate_auth_backend.features.auth.presentation.dto.SignUpResponse
import `in`.elabs.certificate_auth_backend.features.auth.presentation.dto.TokenPair
import `in`.elabs.certificate_auth_backend.util.HashPassEncoder
import jakarta.transaction.Transactional
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException
import java.security.MessageDigest
import java.time.Instant
import java.util.*

@Service
class AuthService(
    private val userRepo: UserRepo,
    private val organisationRepo: OrganisationRepo,
    private val authCodeRepo: AuthCodeRepo,
    private val hashPassEncoder: HashPassEncoder,
    private val jwtService: JWTService,
    private val refreshTokenRepo: RefreshTokenRepo
) {

    @Transactional
    fun storeRefreshToken(user: UserModel, rawRefreshToken: String){
        val hashedRefreshToken = hashToken(rawRefreshToken)
        val expiryMs = jwtService.refreshTokenValidityMs
        val expiresAt = Instant.now().plusMillis(expiryMs)
        refreshTokenRepo.save(
            RefreshTokenModel(
                expiresAt = expiresAt,
                hashedToken = hashedRefreshToken,
                user = user
            )
        )
    }

    private fun hashToken(token: String): String{
        val digest = MessageDigest.getInstance("SHA-256")
        val hashByte = digest.digest(token.encodeToByteArray())
        return Base64.getEncoder().encodeToString(hashByte)
    }
    private fun validateUserCredentials(email: String, password: String): Boolean {
        // reject blank or malformed input early
        if (email.isBlank() || password.isBlank() || !isValidEmail(email)) return false
        val trimmedEmail = email.trim()
        val user = userRepo.findByEmail(trimmedEmail) ?: return false
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
        val existingUser = userRepo.findByEmail(request.userEmail)
        if (existingUser != null) {
            throw ResponseStatusException(
                HttpStatus.CONFLICT,
                "User already exists"
            )
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

    @Transactional
    fun loginUser(request: LoginRequest): TokenPair {
        val trimmedEmail = request.userEmail.trim()
        if (!isValidEmail(trimmedEmail)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid email format")
        }
        val user = userRepo.findByEmail(trimmedEmail)?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User not found")
        val isPasswordValid = validateUserCredentials(request.userEmail, request.userPassword)
        if (!isPasswordValid) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid credentials")
        }
        val newAccessToken = jwtService.generateAccessToken(user.id)
        val newRefreshToken = jwtService.generateRefreshToken(user.id)
        storeRefreshToken(user,newRefreshToken)
        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }

    @Transactional
    fun refresh(refreshToken: String): TokenPair{
        if (!jwtService.validateRefreshToken(refreshToken)){
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid Refresh Token")
        }
        val userId = jwtService.getUserIdFromToken(refreshToken)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "User Not Fount")
        val user = userRepo.findById(userId).orElseThrow {
            ResponseStatusException(HttpStatus.NOT_FOUND,"User Not Found")
        }
        val hashed = hashToken(refreshToken)
        refreshTokenRepo.findByUser_IdAndHashedToken(user.id,hashed)
            ?: throw ResponseStatusException(HttpStatus.UNAUTHORIZED,"Refresh Token Not recognised")
        refreshTokenRepo.deleteByUser_IdAndHashedToken(user.id,hashed)
        val newAccessToken = jwtService.generateAccessToken(user.id)
        val newRefreshToken = jwtService.generateRefreshToken(user.id)
        storeRefreshToken(user,newRefreshToken)
        return TokenPair(
            accessToken = newAccessToken,
            refreshToken = newRefreshToken
        )
    }
}