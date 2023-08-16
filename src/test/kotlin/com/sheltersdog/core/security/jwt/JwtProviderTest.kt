package com.sheltersdog.core.security.jwt

import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.IncorrectClaimException
import io.jsonwebtoken.security.SignatureException
import org.bson.types.ObjectId
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

class JwtProviderTest {
    private val jwtProperties = JwtProperties(
        accessToken = "aliuehwf!$#wrgne75ad@#&fatwwaefaeraowirASFASE2.esmgms;l",
        refreshToken = "aliuwrgne7wafl&^%5adfatwesmgmse$^FSAGFSGHaraowir2ehwf!$#.;",
        accessTokenExpiredTime = 3600,
        refreshTokenExpiredTime = 36000,
        issuer = "Sheltersdog",
        subject = "subject"
    )

    @Test
    fun initTest() {
        val jwtProvider = JwtProvider(jwtProperties = jwtProperties)
        assertNotNull(jwtProvider)
        assertEquals(jwtProvider.jwtProperties, jwtProperties)
    }

    @Test
    fun generateTokenTest() {
        val id = UUID.randomUUID().toString()
        val jwtProvider = JwtProvider(jwtProperties = jwtProperties)
        val jwtDto = jwtProvider.generateToken(id)

        val zoneId = ZoneOffset.systemDefault()
        assertEquals(jwtDto.id, id)
        assertNotNull(jwtDto)
        assertNotNull(jwtDto.accessToken)
        assertNotNull(jwtDto.refreshToken)
        assertEquals(jwtDto.accessToken.filter { c -> c == '.' }, "..")
        assertEquals(jwtDto.refreshToken.filter { c -> c == '.' }, "..")

        assertTrue(
            jwtDto.accessTokenExpiredTime > LocalDateTime.now().atZone(zoneId).toEpochSecond()
        )
        assertTrue(
            jwtDto.refreshTokenExpiredTime > LocalDateTime.now().atZone(zoneId).toEpochSecond()
        )
        assertTrue(
            jwtDto.refreshTokenExpiredTime > jwtDto.accessTokenExpiredTime
        )
    }

    @Test
    fun verifyAccessTokenTest() {
        val jwtProvider = JwtProvider(jwtProperties)
        val jwtDto = jwtProvider.generateToken(ObjectId.get().toString())
        assertDoesNotThrow { jwtProvider.verifyAccessToken(jwtDto.accessToken, jwtDto.id) }
    }

    @MethodSource
    @ParameterizedTest(name = "{4}")
    fun verifyAccessTokenExceptionTest(
        jwtProvider: JwtProvider,
        token: String,
        id: String,
        exception: Class<Exception>,
        description: String,
    ) {
        assertThrows(exception) {
            jwtProvider.verifyAccessToken(
                token,
                id
            )
        }
    }

    @Test
    fun verifyRefreshTokenTest() {
        val jwtProvider = JwtProvider(jwtProperties)
        val jwtDto = jwtProvider.generateToken(ObjectId.get().toString())
        assertDoesNotThrow { jwtProvider.verifyRefreshToken(jwtDto.refreshToken, jwtDto.id) }
    }

    @MethodSource
    @ParameterizedTest(name = "{4}")
    fun verifyRefreshTokenExceptionTest(
        jwtProvider: JwtProvider,
        token: String,
        id: String,
        exception: Class<Exception>,
        description: String,
    ) {
        assertThrows(exception) {
            jwtProvider.verifyRefreshToken(
                token,
                id
            )
        }
    }

    @Test
    fun getIdTest() {
        val jwtProvider = JwtProvider(jwtProperties)
        val id = ObjectId.get().toString()
        val jwtDto = jwtProvider.generateToken(id)

        assertEquals(id, jwtProvider.getId(jwtDto.accessToken))
    }

    companion object {
        @JvmStatic
        fun verifyAccessTokenExceptionTest(): List<Arguments> {
            val jwtProperties = JwtProperties(
                accessToken = "aliuehwf!$#wrgne75ad@#&fatwwaefaeraowirASFASE2.esmgms;l",
                refreshToken = "aliuwrgne7wafl&^%5adfatwesmgmse$^FSAGFSGHaraowir2ehwf!$#.;",
                accessTokenExpiredTime = 3600,
                refreshTokenExpiredTime = 36000,
                issuer = "Sheltersdog",
                subject = "subject"
            )
            val jwtProvider = JwtProvider(jwtProperties = jwtProperties)
            val expiredJwtProvider = JwtProvider(
                jwtProperties = jwtProperties.copy(
                    accessTokenExpiredTime = 0L,
                    refreshTokenExpiredTime = 0L
                )
            )

            val jwtDto = jwtProvider.generateToken(ObjectId.get().toString())
            val expiredJwtDto = expiredJwtProvider.generateToken(ObjectId.get().toString())
            return listOf(
                Arguments.of(
                    jwtProvider,
                    jwtDto.refreshToken,
                    jwtDto.id,
                    SignatureException::class.java,
                    "SignatureException 테스트"
                ),
                Arguments.of(
                    jwtProvider,
                    jwtDto.accessToken,
                    ObjectId.get().toString(),
                    IncorrectClaimException::class.java,
                    "IncorrectClaimException 테스트"
                ),
                Arguments.of(
                    jwtProvider,
                    "",
                    jwtDto.id,
                    IllegalArgumentException::class.java,
                    "IllegalArgumentException 테스트 (1)"
                ),
                Arguments.of(
                    jwtProvider,
                    "",
                    "",
                    IllegalArgumentException::class.java,
                    "IllegalArgumentException 테스트 (2)"
                ),
                Arguments.of(
                    expiredJwtProvider,
                    expiredJwtDto.accessToken,
                    expiredJwtDto.id,
                    ExpiredJwtException::class.java,
                    "ExpiredJwtException 테스트"
                ),
            )
        }

        @JvmStatic
        fun verifyRefreshTokenExceptionTest(): List<Arguments> {
            val jwtProperties = JwtProperties(
                accessToken = "aliuehwf!$#wrgne75ad@#&fatwwaefaeraowirASFASE2.esmgms;l",
                refreshToken = "aliuwrgne7wafl&^%5adfatwesmgmse$^FSAGFSGHaraowir2ehwf!$#.;",
                accessTokenExpiredTime = 3600,
                refreshTokenExpiredTime = 36000,
                issuer = "Sheltersdog",
                subject = "subject"
            )
            val jwtProvider = JwtProvider(jwtProperties = jwtProperties)
            val expiredJwtProvider = JwtProvider(
                jwtProperties = jwtProperties.copy(
                    accessTokenExpiredTime = 0L,
                    refreshTokenExpiredTime = 0L
                )
            )

            val jwtDto = jwtProvider.generateToken(ObjectId.get().toString())
            val expiredJwtDto = expiredJwtProvider.generateToken(ObjectId.get().toString())
            return listOf(
                Arguments.of(
                    jwtProvider,
                    jwtDto.accessToken,
                    jwtDto.id,
                    SignatureException::class.java,
                    "SignatureException 테스트"
                ),
                Arguments.of(
                    jwtProvider,
                    jwtDto.refreshToken,
                    ObjectId.get().toString(),
                    IncorrectClaimException::class.java,
                    "IncorrectClaimException 테스트"
                ),
                Arguments.of(
                    jwtProvider,
                    "",
                    jwtDto.id,
                    IllegalArgumentException::class.java,
                    "IllegalArgumentException 테스트 (1)"
                ),
                Arguments.of(
                    jwtProvider,
                    "",
                    "",
                    IllegalArgumentException::class.java,
                    "IllegalArgumentException 테스트 (2)"
                ),
                Arguments.of(
                    expiredJwtProvider,
                    expiredJwtDto.refreshToken,
                    expiredJwtDto.id,
                    ExpiredJwtException::class.java,
                    "ExpiredJwtException 테스트"
                ),
            )
        }
    }
}