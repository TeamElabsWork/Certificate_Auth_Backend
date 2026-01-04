package `in`.elabs.certificate_auth_backend.features.auth.domain

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
        userId: Long,
        type: TokenType,
        expiry: Long
    ): String{
        val now = Date()
        val expiryDate = Date(now.time + expiry)
        return Jwts.builder()
            .subject(userId.toString())
            .claim("type",type.name)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact()
    }

    fun generateAccessToken(userId: Long): String{
        return generateToken(
            userId = userId,
            type = TokenType.ACCESS_TOKEN,
            expiry = accessTokenValidityMs
        )
    }

    fun generateRefreshToken(userId: Long): String{
        return generateToken(
            userId = userId,
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