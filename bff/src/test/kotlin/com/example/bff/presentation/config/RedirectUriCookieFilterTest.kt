package com.example.bff.presentation.config

import com.example.bff.integration.config.AppProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

/**
 * [RedirectUriCookieFilter] のテストクラス。
 * 
 * ログイン成功後のリダイレクト先を制御するCookieの発行ロジックと、
 * オープンリダイレクト攻撃に対するバリデーションが正しく機能することを検証します。
 */
class RedirectUriCookieFilterTest {

    private val appProperties = AppProperties(
        frontendOrigin = "http://localhost:3000",
        bffOrigin = "http://localhost:8080",
        apiBaseUrl = "http://api:8080",
        keycloakLogoutUrl = "http://keycloak:8080/realms/lumina/protocol/openid-connect/logout"
    )

    private val filter = RedirectUriCookieFilter(appProperties)

    /** リクエスト交換オブジェクトの生成ヘルパー */
    private fun createExchange(path: String, redirectUri: String? = null): MockServerWebExchange {
        val builder = MockServerHttpRequest.get(path)
        if (redirectUri != null) builder.queryParam("redirect_uri", redirectUri)
        return MockServerWebExchange.from(builder.build())
    }

    /** フィルターチェーンのモック生成ヘルパー */
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

        StepVerifier.create(result).verifyComplete()

        // Cookieが発行されていることを検証
        val cookie = exchange.response.cookies.getFirst(RedirectUriCookieFilter.POST_LOGIN_REDIRECT_URI_COOKIE)
        assertThat(cookie).isNotNull
        assertThat(cookie?.value).isEqualTo("/dashboard")
        Mockito.verify(chain).filter(exchange)
    }

    @Test
    @DisplayName("不正な redirect_uri の場合 Cookie が保存されないこと（バリデーション確認）")
    fun filter_invalidRedirectUri_doesNotSaveCookie() {
        // 不正なパターンのリスト
        val invalidUris = listOf(
            "dashboard",           // / で始まらない
            "//evil.com",          // プロトコル相対
            "http://evil.com",     // 絶対パス
            "https://evil.com",
            "/\\evil.com",         // バックスラッシュ
            "/\npath"              // 制御文字
        )

        invalidUris.forEach { uri ->
            val exchange = createExchange("/oauth2/authorization/keycloak", uri)
            val chain = mockChain(exchange)

            filter.filter(exchange, chain).block() // 同期的に実行して検証

            val cookie = exchange.response.cookies.getFirst(RedirectUriCookieFilter.POST_LOGIN_REDIRECT_URI_COOKIE)
            assertThat(cookie).isNull() // Cookieが設定されていないことを確認
        }
    }

    @Test
    @DisplayName("対象外パス・パラメータなしの場合は後続フィルターへ委譲されること")
    fun filter_noActionRequired_delegatesToChain() {
        // パラメータなしケース
        val exchange1 = createExchange("/oauth2/authorization/keycloak")
        val chain1 = mockChain(exchange1)
        filter.filter(exchange1, chain1).block()
        Mockito.verify(chain1).filter(exchange1)

        // 対象外パスケース
        val exchange2 = createExchange("/auth/me", "/dashboard")
        val chain2 = mockChain(exchange2)
        filter.filter(exchange2, chain2).block()
        Mockito.verify(chain2).filter(exchange2)
    }
}