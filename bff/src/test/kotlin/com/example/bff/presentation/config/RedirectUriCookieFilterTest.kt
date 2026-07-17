package com.example.bff.presentation.config

import com.example.bff.integration.config.AppProperties
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class RedirectUriCookieFilterTest {

    private val appProperties = AppProperties(
        frontendOrigin = "http://localhost:3000",
        bffOrigin = "http://localhost:8080",
        apiBaseUrl = "http://api:8080",
        keycloakLogoutUrl = "http://keycloak:8080/realms/lumina/protocol/openid-connect/logout"
    )

    private val filter = RedirectUriCookieFilter(appProperties)

    private fun createExchange(path: String, redirectUri: String? = null): MockServerWebExchange {
        val builder = MockServerHttpRequest.get(path)
        if (redirectUri != null) {
            builder.queryParam("redirect_uri", redirectUri)
        }
        return MockServerWebExchange.from(builder.build())
    }

    private fun mockChain(exchange: MockServerWebExchange): WebFilterChain {
        val chain = Mockito.mock(WebFilterChain::class.java)
        Mockito.`when`(chain.filter(exchange)).thenReturn(Mono.empty())
        return chain
    }

    @Test
    @DisplayName("正当な redirect_uri が POST_LOGIN_REDIRECT_URI Cookie に保存されること")
    fun filter_validRedirectUri_savesCookie() {
        val exchange = createExchange("/oauth2/authorization/keycloak", "/dashboard")
        val chain = mockChain(exchange)

        val result = filter.filter(exchange, chain)

        StepVerifier.create(result)
            .verifyComplete()

        Mockito.verify(chain, Mockito.times(1)).filter(exchange)
    }

    @Test
    @DisplayName("redirect_uri が / から始まらない場合 Cookie が保存されないこと")
    fun filter_redirectUriNotStartingWithSlash_doesNotSaveCookie() {
        val exchange = createExchange("/oauth2/authorization/keycloak", "dashboard")
        val chain = mockChain(exchange)

        val result = filter.filter(exchange, chain)

        StepVerifier.create(result)
            .verifyComplete()
    }

    @Test
    @DisplayName("redirect_uri が // から始まる場合 Cookie が保存されないこと")
    fun filter_redirectUriStartingWithDoubleSlash_doesNotSaveCookie() {
        val exchange = createExchange("/oauth2/authorization/keycloak", "//evil.com")
        val chain = mockChain(exchange)

        val result = filter.filter(exchange, chain)

        StepVerifier.create(result)
            .verifyComplete()
    }

    @Test
    @DisplayName("redirect_uri に http:// を含む場合 Cookie が保存されないこと")
    fun filter_redirectUriContainingHttp_doesNotSaveCookie() {
        val exchange = createExchange("/oauth2/authorization/keycloak", "http://evil.com")
        val chain = mockChain(exchange)

        val result = filter.filter(exchange, chain)

        StepVerifier.create(result)
            .verifyComplete()
    }

    @Test
    @DisplayName("redirect_uri に https:// を含む場合 Cookie が保存されないこと")
    fun filter_redirectUriContainingHttps_doesNotSaveCookie() {
        val exchange = createExchange("/oauth2/authorization/keycloak", "https://evil.com")
        val chain = mockChain(exchange)

        val result = filter.filter(exchange, chain)

        StepVerifier.create(result)
            .verifyComplete()
    }

    @Test
    @DisplayName("redirect_uri に \\ を含む場合 Cookie が保存されないこと")
    fun filter_redirectUriContainingBackslash_doesNotSaveCookie() {
        val exchange = createExchange("/oauth2/authorization/keycloak", "/\\evil.com")
        val chain = mockChain(exchange)

        val result = filter.filter(exchange, chain)

        StepVerifier.create(result)
            .verifyComplete()
    }

    @Test
    @DisplayName("redirect_uri に制御文字を含む場合 Cookie が保存されないこと")
    fun filter_redirectUriContainingControlChar_doesNotSaveCookie() {
        val exchange = createExchange("/oauth2/authorization/keycloak", "/\npath")
        val chain = mockChain(exchange)

        val result = filter.filter(exchange, chain)

        StepVerifier.create(result)
            .verifyComplete()
    }

    @Test
    @DisplayName("redirect_uri クエリパラメータが存在しない場合後続フィルターへ委譲されること")
    fun filter_noRedirectUriParam_delegatesToChain() {
        val exchange = createExchange("/oauth2/authorization/keycloak")
        val chain = mockChain(exchange)

        val result = filter.filter(exchange, chain)

        StepVerifier.create(result)
            .verifyComplete()

        Mockito.verify(chain, Mockito.times(1)).filter(exchange)
    }

    @Test
    @DisplayName("対象外パスの場合後続フィルターへ委譲されること")
    fun filter_nonMatchingPath_delegatesToChain() {
        val exchange = createExchange("/auth/me", "/dashboard")
        val chain = mockChain(exchange)

        val result = filter.filter(exchange, chain)

        StepVerifier.create(result)
            .verifyComplete()

        Mockito.verify(chain, Mockito.times(1)).filter(exchange)
    }
}