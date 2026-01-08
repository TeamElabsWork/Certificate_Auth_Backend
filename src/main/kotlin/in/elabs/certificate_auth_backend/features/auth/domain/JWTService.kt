package `in`.elabs.certificate_auth_backend.features.auth.domain

import `in`.elabs.certificate_auth_backend.features.auth.data.model.Role
import `in`.elabs.certificate_auth_backend.features.auth.data.model.UserModel
import `in`.elabs.certificate_auth_backend.util.TokenType
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.util.*

@Service
class JWTService(
    @Value("\${JWT_SECRET_BASE64}") private val jwtSecret: String
) {

    private val secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(jwtSecret))
    private val accessTokenValidityMs = 15L * 60L * 1000L
    val refreshTokenValidityMs = 30L * 24L * 60L * 60L * 1000L

    private fun generateToken(
        user: UserModel,
        type: TokenType,
        expiry: Long
    ): String{
        val now = Date()
        val expiryDate = Date(now.time + expiry)
        return Jwts.builder()
            .subject(user.id.toString())
            .claim("type",type.name)
            .claim("roles", user.roles.map { it.name })
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }

    fun generateAccessToken(user: UserModel): String{
        return generateToken(
            user = user,
            type = TokenType.ACCESS_TOKEN,
            expiry = accessTokenValidityMs
        )
    }

    fun generateRefreshToken(user: UserModel): String{
        return generateToken(
            user = user,
            type = TokenType.REFRESH_TOKEN,
            expiry = refreshTokenValidityMs
        )
    }

    fun validateAccessToken(token: String): Boolean{
        val claims = parseAllClaims(token) ?: return  false
        val tokenType = claims["type"] as? String ?: return false
        return tokenType == TokenType.ACCESS_TOKEN.name
    }

    fun validateRefreshToken(token: String): Boolean{
        val claims = parseAllClaims(token) ?: return  false
        val tokenType = claims["type"] as? String ?: return false
        return tokenType == TokenType.REFRESH_TOKEN.name
    }

    fun getUserIdFromToken(token: String): Long? {
        val claims = parseAllClaims(token) ?: return null
        return claims.subject.toLongOrNull()
    }

    fun getUserRoles(token: String): Set<Role> {
        val claims = parseAllClaims(token) ?: return emptySet()

        val roles = claims["roles"] as? List<*> ?: return emptySet()

        return roles
            .mapNotNull { role ->
                runCatching { Role.valueOf(role.toString()) }.getOrNull()
            }
            .toSet()
    }

    private fun parseAllClaims(token: String): Claims?{
        return try{
            Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (_: Exception){
            null
        }
    }
}