package com.example.bff.presentation.config

import com.example.bff.integration.config.AppProperties
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.http.HttpStatus
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.oidc.OidcIdToken
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.web.server.WebFilterChain
import reactor.test.StepVerifier
import java.net.URLEncoder

class CustomLogoutSuccessHandlerTest {

    private val appProperties = AppProperties(
        frontendOrigin = "http://localhost:3000",
        bffOrigin = "http://localhost:8080",
        apiBaseUrl = "http://api:8080",
        keycloakLogoutUrl = "http://keycloak:8080/realms/lumina/protocol/openid-connect/logout"
    )

    private val handler = CustomLogoutSuccessHandler(appProperties)

    @Test
    @DisplayName("ログアウト成功時 idToken ありの場合 Location に id_token_hint と post_logout_redirect_uri が含まれること")
    fun onLogoutSuccess_withIdToken_includesIdTokenHintInLocation() {
        val request = MockServerHttpRequest.get("/logout").build()
        val exchange = MockServerWebExchange.from(request)
        val chain = Mockito.mock(WebFilterChain::class.java)
        val webFilterExchange = WebFilterExchange(exchange, chain)

        val oidcUser = Mockito.mock(OidcUser::class.java)
        val idToken = Mockito.mock(OidcIdToken::class.java)
        Mockito.`when`(idToken.tokenValue).thenReturn("dummy-id-token")
        Mockito.`when`(oidcUser.idToken).thenReturn(idToken)

        val authentication = Mockito.mock(OAuth2AuthenticationToken::class.java)
        Mockito.`when`(authentication.principal).thenReturn(oidcUser)

        val result = handler.onLogoutSuccess(webFilterExchange, authentication)

        StepVerifier.create(result)
            .verifyComplete()

        val response = exchange.response
        assert(response.statusCode == HttpStatus.FOUND) {
            "Expected 302 FOUND but got ${response.statusCode}"
        }

        val location = response.headers.location.toString()
        assert(location.contains("id_token_hint=dummy-id-token")) {
            "Expected Location to contain 'id_token_hint=dummy-id-token' but got '$location'"
        }

        val expectedRedirectUri = URLEncoder.encode("http://localhost:3000/", "UTF-8")
        assert(location.contains("post_logout_redirect_uri=$expectedRedirectUri")) {
            "Expected Location to contain 'post_logout_redirect_uri=$expectedRedirectUri' but got '$location'"
        }
    }

    @Test
    @DisplayName("ログアウト成功時 idToken なしの場合 Location に id_token_hint が含まれないこと")
    fun onLogoutSuccess_withoutIdToken_doesNotIncludeIdTokenHintInLocation() {
        val request = MockServerHttpRequest.get("/logout").build()
        val exchange = MockServerWebExchange.from(request)
        val chain = Mockito.mock(WebFilterChain::class.java)
        val webFilterExchange = WebFilterExchange(exchange, chain)

        // OAuth2AuthenticationToken ではない Authentication（idTokenなし）
        val authentication = Mockito.mock(Authentication::class.java)

        val result = handler.onLogoutSuccess(webFilterExchange, authentication)

        StepVerifier.create(result)
            .verifyComplete()

        val response = exchange.response
        assert(response.statusCode == HttpStatus.FOUND) {
            "Expected 302 FOUND but got ${response.statusCode}"
        }

        val location = response.headers.location.toString()
        assert(!location.contains("id_token_hint")) {
            "Expected Location NOT to contain 'id_token_hint' but got '$location'"
        }

        val expectedRedirectUri = URLEncoder.encode("http://localhost:3000/", "UTF-8")
        assert(location.contains("post_logout_redirect_uri=$expectedRedirectUri")) {
            "Expected Location to contain 'post_logout_redirect_uri=$expectedRedirectUri' but got '$location'"
        }
    }
}