package com.sheltersdog.core.security.jwt

import com.sheltersdog.core.dto.JwtDto
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

    fun generateToken(id: Long): JwtDto {
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

    private fun generateJwt(id: Long, key: Key, expiredTime: LocalDateTime): String {
        return Jwts.builder()
            .setHeader(
                mapOf<String, String>(
                    "typ" to "JWT",
                    "alg" to "HS256"
                )
            ).addClaims(
                mapOf<String, Long>(
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

    fun verifyAccessToken(jwt: String, id: Long) {
        verifyToken(
            jwt = jwt,
            id = id,
            key = accessKey
        )
    }

    fun verifyRefreshToken(jwt: String, id: Long) {
        verifyToken(
            jwt = jwt,
            id = id,
            key = refreshKey
        )
    }

    private fun verifyToken(jwt: String, id: Long, key: Key) {
        Jwts.parserBuilder()
            .setSigningKey(key)
            .requireId(id.toString())
            .requireSubject(jwtProperties.subject)
            .requireIssuer(jwtProperties.issuer)
            .build()
            .parse(jwt)
    }

    fun getId(jwt: String): Long {
        return parseAccessToken(jwt, accessKey)["id"].toString().toLong()
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

        throw SignatureException("token is not mine!!")
    }


}