package com.sheltersdog.core.security

import com.sheltersdog.core.properties.CorsProperties
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.MapReactiveUserDetailsService
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.core.userdetails.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

/**
 * https://sthwin.tistory.com/24
 */
@Configuration
@EnableWebFluxSecurity
class SecurityConfig @Autowired constructor(
    val jwtFilter: JwtFilter,
    val corsProperties: CorsProperties,
) {

    @Bean
    fun userDetailsService(): ReactiveUserDetailsService {
        val grantedAuthorities: Collection<GrantedAuthority?> = listOf(SimpleGrantedAuthority("ROLE_USER"))
        val userDetails = User.withUserDetails(
            User("user", "password", grantedAuthorities)
        ).build()

        return MapReactiveUserDetailsService(userDetails)
    }

    @Bean
    fun securityFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http.securityMatcher(
            PathPatternParserServerWebExchangeMatcher("/api/**", HttpMethod.GET)
        ).authorizeExchange {
            it.pathMatchers(HttpMethod.GET, "/api/**").permitAll()
        }.apply {
            this.addFilterBefore(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
        }.build()
    }


    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val corsConfiguration = CorsConfiguration()

        corsConfiguration.allowCredentials = true

        corsProperties.allowedOrigins.forEach { origin -> corsConfiguration.addAllowedOrigin(origin) }
        corsConfiguration.addAllowedMethod("*")
        corsConfiguration.addAllowedHeader("*")
        corsConfiguration.addExposedHeader("*")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", corsConfiguration)
        return source
    }
}