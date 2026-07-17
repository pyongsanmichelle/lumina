package com.example.bff.presentation.config

import com.example.bff.integration.config.AppProperties
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.http.HttpCookie
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.web.server.WebFilterChain
import reactor.test.StepVerifier

class CustomAuthenticationSuccessHandlerTest {

    private val appProperties = AppProperties(
        frontendOrigin = "http://localhost:3000",
        bffOrigin = "http://localhost:8080",
        apiBaseUrl = "http://api:8080",
        keycloakLogoutUrl = "http://keycloak:8080/realms/lumina/protocol/openid-connect/logout"
    )

    private val handler = CustomAuthenticationSuccessHandler(appProperties)

    @Test
    @DisplayName("認証成功時 POST_LOGIN_REDIRECT_URI Cookie ありの場合 302 でそのパスへリダイレクトしCookieを削除すること")
    fun onAuthenticationSuccess_withRedirectCookie_redirectsToCookiePathAndRemovesCookie() {
        val request = MockServerHttpRequest.get("/oauth2/authorization/keycloak")
            .cookie(HttpCookie(RedirectUriCookieFilter.POST_LOGIN_REDIRECT_URI_COOKIE, "/dashboard"))
            .build()
        val exchange = MockServerWebExchange.from(request)
        val chain = Mockito.mock(WebFilterChain::class.java)
        val webFilterExchange = WebFilterExchange(exchange, chain)
        val authentication = Mockito.mock(Authentication::class.java)

        val result = handler.onAuthenticationSuccess(webFilterExchange, authentication)

        StepVerifier.create(result)
            .verifyComplete()

        val response = exchange.response
        assert(response.statusCode == HttpStatus.FOUND) {
            "Expected 302 FOUND but got ${response.statusCode}"
        }

        val location = response.headers.location
        assert(location != null) {
            "Expected Location header to be present"
        }
        assert(location.toString() == "http://localhost:3000/dashboard") {
            "Expected Location 'http://localhost:3000/dashboard' but got '$location'"
        }

        // Cookieが削除されていることを確認（MapからremoveされたのでgetFirstはnull）
        assert(exchange.response.cookies.isEmpty()) {
            "Expected cookies to be empty after removal"
        }
    }

    @Test
    @DisplayName("認証成功時 POST_LOGIN_REDIRECT_URI Cookie なしの場合 302 でデフォルトパス / へリダイレクトすること")
    fun onAuthenticationSuccess_withoutRedirectCookie_redirectsToDefaultPath() {
        val request = MockServerHttpRequest.get("/oauth2/authorization/keycloak").build()
        val exchange = MockServerWebExchange.from(request)
        val chain = Mockito.mock(WebFilterChain::class.java)
        val webFilterExchange = WebFilterExchange(exchange, chain)
        val authentication = Mockito.mock(Authentication::class.java)

        val result = handler.onAuthenticationSuccess(webFilterExchange, authentication)

        StepVerifier.create(result)
            .verifyComplete()

        val response = exchange.response
        assert(response.statusCode == HttpStatus.FOUND) {
            "Expected 302 FOUND but got ${response.statusCode}"
        }

        val location = response.headers.location
        assert(location != null) {
            "Expected Location header to be present"
        }
        assert(location.toString() == "http://localhost:3000/") {
            "Expected Location 'http://localhost:3000/' but got '$location'"
        }
    }
}