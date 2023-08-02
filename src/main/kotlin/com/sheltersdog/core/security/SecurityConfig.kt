package com.sheltersdog.core.security

import com.sheltersdog.core.properties.ActiveProperties
import com.sheltersdog.core.properties.CorsProperties
import com.sheltersdog.core.properties.GatewayProperties
import com.sheltersdog.core.security.jwt.JwtProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.actuate.endpoint.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.Customizer
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
import org.springframework.security.web.server.context.ServerSecurityContextRepository
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * https://sthwin.tistory.com/24
 */
@Configuration
@EnableWebFluxSecurity
class SecurityConfig @Autowired constructor(
    val corsProperties: CorsProperties,
    val jwtFilter: JwtFilter,
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
        return http
            .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .csrf { it.disable() }
//            .securityMatcher(PathPatternParserServerWebExchangeMatcher("/**", HttpMethod.GET))
//            .authorizeExchange { exchanges ->
//                exchanges.pathMatchers(HttpMethod.GET, "/private/**").hasRole("USER")
//                exchanges.pathMatchers(HttpMethod.PUT, "/public/**").permitAll()
//                exchanges.pathMatchers(HttpMethod.POST, "/public/**").permitAll()
//                exchanges.pathMatchers(HttpMethod.DELETE, "/public/**").permitAll()
//
//                exchanges.pathMatchers(HttpMethod.GET, "/**").permitAll()
//                exchanges.pathMatchers(HttpMethod.PUT, "/**").hasRole("USER")
//                exchanges.pathMatchers(HttpMethod.POST, "/**").hasRole("USER")
//                exchanges.pathMatchers(HttpMethod.DELETE, "/**").hasRole("USER")
//            }
            .build()
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