package com.sheltersdog.core.security

import com.sheltersdog.core.model.AUTHORIZATION
import com.sheltersdog.core.model.BEARER
import com.sheltersdog.core.properties.ActiveProperties
import com.sheltersdog.core.properties.GatewayProperties
import com.sheltersdog.core.security.jwt.JwtProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.buffer.DataBuffer
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.User
import org.springframework.stereotype.Component
import org.springframework.util.StringUtils
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

@Component
class JwtFilter @Autowired constructor(
    val jwtProvider: JwtProvider,
    val activeProperties: ActiveProperties,
    val gatewayProperties: GatewayProperties,
) : WebFilter {

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        if (
            activeProperties.active != "local"
            && activeProperties.active != "default"
            && activeProperties.active != "test"
        ) {
            val value = exchange.request.headers[gatewayProperties.key]
            if (value.isNullOrEmpty() || !value.first().equals(gatewayProperties.value)) {
                return filterChainError(exchange, "잘못된 경로로 요청하였습니다.")
            }
        }

        val authorizationWrapper = exchange.request.headers[AUTHORIZATION]
        if (authorizationWrapper.isNullOrEmpty()) return chain.filter(exchange)

        val jwt = getJwt(authorizationWrapper.first())
        saveContextAuthentication(jwt)
        return chain.filter(exchange)
    }

    private fun filterChainError(exchange: ServerWebExchange, message: String): Mono<Void> {
        exchange.response.statusCode = HttpStatus.UNAUTHORIZED
        val buffer: DataBuffer = exchange.response.bufferFactory().wrap(message.encodeToByteArray())
        return exchange.response.writeWith(Mono.just(buffer))
    }

    private fun getJwt(authorization: String): String {
        if (StringUtils.hasText(authorization) && authorization.startsWith(BEARER)) {
            return authorization.substring(7)
        }

        return ""
    }


    private fun saveContextAuthentication(jwt: String) {
        if (!StringUtils.hasText(jwt)) return
        val id: Long = jwtProvider.getId(jwt)
        val authentication = generateAuthentication(jwt, id)
        SecurityContextHolder.getContext().authentication = authentication
    }

    private fun generateAuthentication(jwt: String, id: Long): Authentication {
        val grantedAuthorities = listOf(SimpleGrantedAuthority("ROLE_USER"))
        val user = User(id.toString(), "", grantedAuthorities)
        return UsernamePasswordAuthenticationToken(user, jwt, grantedAuthorities)
    }
}