package com.example.bff.presentation.config

import com.example.bff.integration.config.AppProperties
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.net.URI
import java.net.URLEncoder

/**
 * RP-Initiated Logout の成功ハンドラ。
 * Keycloak の end_session_endpoint に id_token_hint 付きでリダイレクトする。
 */
@Component
class CustomLogoutSuccessHandler(
    private val appProperties: AppProperties
) : ServerLogoutSuccessHandler {

    override fun onLogoutSuccess(
        exchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        // ★ replaceを使った文字列操作を削除し、AppPropertiesから取得
        val keycloakLogoutUrl = appProperties.keycloakLogoutUrl
        val redirectUri = appProperties.frontendOrigin + "/"

        // id_token_hint を取得
        val idToken = (authentication as? OAuth2AuthenticationToken)
            ?.let { token ->
                val oidcUser = token.principal as? OidcUser
                oidcUser?.idToken?.tokenValue
            }

        val url = if (idToken != null) {
            "$keycloakLogoutUrl?id_token_hint=$idToken&post_logout_redirect_uri=${URLEncoder.encode(redirectUri, "UTF-8")}"
        } else {
            "$keycloakLogoutUrl?post_logout_redirect_uri=${URLEncoder.encode(redirectUri, "UTF-8")}"
        }

        val response = exchange.exchange.response
        response.statusCode = HttpStatus.FOUND
        response.headers.location = URI.create(url)
        return response.setComplete()
    }
}