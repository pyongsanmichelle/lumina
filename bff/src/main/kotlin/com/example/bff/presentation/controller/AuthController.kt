package com.example.bff.presentation.controller

import com.example.bff.presentation.dto.AuthResponse
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * 認証状態確認API。
 * GET /bff/auth/me を提供する。
 */
@RestController
class AuthController {

    @GetMapping("/auth/me", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAuthStatus(
        @AuthenticationPrincipal principal: OidcUser?
    ): Mono<AuthResponse> = principal?.let { user ->
        Mono.just(
            AuthResponse(
                authenticated = true,
                userId = user.subject,
                username = user.preferredUsername,
                roles = user.authorities.mapNotNull { it.authority }
            )
        )
    } ?: Mono.just(AuthResponse(authenticated = false))
}