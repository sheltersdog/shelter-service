package com.sheltersdog.core.security.jwt

import com.sheltersdog.core.dto.JwtDto
import com.sheltersdog.core.exception.ExceptionType
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.SignatureException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import java.security.Key
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*
import javax.crypto.spec.SecretKeySpec

@Component
class JwtProvider(@Autowired val jwtProperties: JwtProperties) {
    private val accessKey: Key
    private val refreshKey: Key

    init {
        accessKey = SecretKeySpec(
            Base64.getEncoder().encode(jwtProperties.accessToken.encodeToByteArray()),
            SignatureAlgorithm.HS512.jcaName
        )
        refreshKey = SecretKeySpec(
            Base64.getEncoder().encode(jwtProperties.refreshToken.encodeToByteArray()),
            SignatureAlgorithm.HS512.jcaName
        )
    }

    fun generateToken(id: String): JwtDto {
        val accessTokenExpiredTime = LocalDateTime.now().plusSeconds(jwtProperties.accessTokenExpiredTime)
        val refreshTokenExpiredTime = LocalDateTime.now().plusSeconds(jwtProperties.refreshTokenExpiredTime)

        val zoneId = ZoneOffset.systemDefault()
        return JwtDto(
            id = id,
            accessToken = generateJwt(
                id = id,
                key = accessKey,
                expiredTime = accessTokenExpiredTime
            ),
            refreshToken = generateJwt(
                id = id,
                key = refreshKey,
                expiredTime = refreshTokenExpiredTime
            ),
            accessTokenExpiredTime = accessTokenExpiredTime.atZone(zoneId).toEpochSecond(),
            refreshTokenExpiredTime = refreshTokenExpiredTime.atZone(zoneId).toEpochSecond(),
        )
    }

    private fun generateJwt(id: String, key: Key, expiredTime: LocalDateTime): String {
        return Jwts.builder()
            .setHeader(
                mapOf<String, String>(
                    "typ" to "JWT",
                    "alg" to "HS256"
                )
            ).addClaims(
                mapOf<String, String>(
                    "id" to id
                )
            )
            .setIssuedAt(Timestamp.valueOf(LocalDateTime.now()))
            .setId(id.toString())
            .setIssuer(jwtProperties.issuer)
            .setSubject(jwtProperties.subject)
            .setExpiration(Timestamp.valueOf(expiredTime))
            .signWith(key, SignatureAlgorithm.HS512)
            .compact()
    }

    /**
     * MalformedJwtException – if the specified JWT was incorrectly constructed (and therefore invalid). Invalid JWTs should not be trusted and should be discarded.
     * SignatureException – if a JWS signature was discovered, but could not be verified. JWTs that fail signature validation should not be trusted and should be discarded.
     * ExpiredJwtException – if the specified JWT is a Claims JWT and the Claims has an expiration time before the time this method is invoked.
     * IllegalArgumentException – if the specified string is null or empty or only whitespace.
     */
    fun verifyAccessToken(jwt: String, id: String) {
        verifyToken(
            jwt = jwt,
            id = id,
            key = accessKey
        )
    }


    /**
     * MalformedJwtException – if the specified JWT was incorrectly constructed (and therefore invalid). Invalid JWTs should not be trusted and should be discarded.
     * SignatureException – if a JWS signature was discovered, but could not be verified. JWTs that fail signature validation should not be trusted and should be discarded.
     * ExpiredJwtException – if the specified JWT is a Claims JWT and the Claims has an expiration time before the time this method is invoked.
     * IllegalArgumentException – if the specified string is null or empty or only whitespace.
     */
    fun verifyRefreshToken(jwt: String, id: String) {
        verifyToken(
            jwt = jwt,
            id = id,
            key = refreshKey
        )
    }

    private fun verifyToken(jwt: String, id: String, key: Key) {
        Jwts.parserBuilder()
            .setSigningKey(key)
            .requireId(id)
            .requireSubject(jwtProperties.subject)
            .requireIssuer(jwtProperties.issuer)
            .build()
            .parse(jwt)
    }

    fun getId(jwt: String): String {
        return parseAccessToken(jwt, accessKey)["id"].toString()
    }

    private fun parseAccessToken(jwt: String, key: Key): Map<*, *> {
        val body: Any = Jwts.parserBuilder()
            .setSigningKey(key)
            .requireSubject(jwtProperties.subject)
            .requireIssuer(jwtProperties.issuer)
            .build()
            .parse(jwt)
            .body
        if (body is Map<*, *>) return body

        throw SignatureException(ExceptionType.TOKEN_PARSE_EXCEPTION.message)
    }


}