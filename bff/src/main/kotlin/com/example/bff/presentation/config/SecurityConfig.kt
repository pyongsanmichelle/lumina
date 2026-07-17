package com.example.bff.presentation.config

import com.example.bff.integration.config.AppProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource

@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val appProperties: AppProperties,
    private val customLogoutSuccessHandler: CustomLogoutSuccessHandler
) {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        val redirectFilter = RedirectUriCookieFilter(appProperties)

        return http
            .oauth2Login { }
            .logout { logout ->
                logout.logoutHandler(SecurityContextServerLogoutHandler())
                logout.logoutSuccessHandler(customLogoutSuccessHandler)
            }
            .csrf { csrf ->
                csrf.csrfTokenRepository(
                    CookieServerCsrfTokenRepository.withHttpOnlyFalse().apply {
                        setCookiePath("/")
                    }
                )
            }
            .cors { cors ->
                cors.configurationSource(CorsConfigurationSource { _ ->
                    CorsConfiguration().apply {
                        addAllowedOrigin(appProperties.frontendOrigin)
                        allowCredentials = true
                        addAllowedMethod("GET")
                        addAllowedMethod("POST")
                        addAllowedMethod("PUT")
                        addAllowedMethod("DELETE")
                        addAllowedMethod("OPTIONS")
                        addAllowedHeader("Content-Type")
                        addAllowedHeader("Authorization")
                        addAllowedHeader("X-XSRF-TOKEN")
                    }
                })
            }
            .exceptionHandling { exceptionHandling ->
                exceptionHandling.authenticationEntryPoint(CustomAuthenticationEntryPoint())
            }
            .authorizeExchange { authorize ->
                authorize.pathMatchers("/auth/me").authenticated()
                authorize.pathMatchers("/health").permitAll()
                authorize.pathMatchers("/oauth2/**").permitAll()
                authorize.pathMatchers("/login/**").permitAll()
                authorize.pathMatchers("/logout").permitAll()
                authorize.anyExchange().authenticated()
            }
            .addFilterBefore(redirectFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }
}