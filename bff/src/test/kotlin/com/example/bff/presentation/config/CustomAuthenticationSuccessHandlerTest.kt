package com.example.bff.presentation.config

import com.example.bff.integration.config.AppProperties
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.http.HttpStatus
import org.springframework.http.HttpCookie
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.web.server.WebFilterChain
import reactor.test.StepVerifier

/**
 * [CustomAuthenticationSuccessHandler] のテストクラス。
 * 
 * 認証成功後のリダイレクト処理において、一時的なクッキーの存在有無による
 * リダイレクト先の決定と、クッキーの適切な削除処理を検証します。
 */
class CustomAuthenticationSuccessHandlerTest {

    // テスト用の設定値を準備
    private val appProperties = AppProperties(
        frontendOrigin = "http://localhost:3000",
        bffOrigin = "http://localhost:8080",
        apiBaseUrl = "http://api:8080",
        keycloakLogoutUrl = "http://keycloak:8080/realms/lumina/protocol/openid-connect/logout"
    )

    private val handler = CustomAuthenticationSuccessHandler(appProperties)

    /**
     * 認証後の遷移先指定Cookie（POST_LOGIN_REDIRECT_URI）が存在する場合の検証。
     * 指定されたパスへリダイレクトし、Cookieが無効化（削除）されることを確認します。
     */
    @Test
    @DisplayName("認証成功時: Cookieありならそのパスへリダイレクトし、Cookie削除用のレスポンスを返すこと")
    fun onAuthenticationSuccess_withRedirectCookie_redirectsToCookiePathAndClearsCookie() {
        // Arrange: リダイレクト先情報を保持したCookieを含むリクエストを作成
        val request = MockServerHttpRequest.get("/oauth2/authorization/keycloak")
            .cookie(HttpCookie(RedirectUriCookieFilter.POST_LOGIN_REDIRECT_URI_COOKIE, "/dashboard"))
            .build()
        val exchange = MockServerWebExchange.from(request)
        val chain = Mockito.mock(WebFilterChain::class.java)
        val webFilterExchange = WebFilterExchange(exchange, chain)
        val authentication = Mockito.mock(Authentication::class.java)

        // Act: 成功ハンドラを実行
        val result = handler.onAuthenticationSuccess(webFilterExchange, authentication)

        // Assert: レスポンスの検証
        StepVerifier.create(result).verifyComplete()

        val response = exchange.response
        // ステータスコードが 302 であること
        assertThat(response.statusCode).isEqualTo(HttpStatus.FOUND)
        // Locationヘッダが指定したパス（/dashboard）を含むこと
        assertThat(response.headers.location.toString()).isEqualTo("http://localhost:3000/dashboard")

        // Cookie削除の検証: maxAge=0 で設定されている（ブラウザに削除させる指示が出ている）か確認
        val cookie = response.cookies.getFirst(RedirectUriCookieFilter.POST_LOGIN_REDIRECT_URI_COOKIE)
        assertThat(cookie).isNotNull
        assertThat(cookie?.maxAge?.seconds).isEqualTo(0)
    }

    /**
     * 遷移先指定Cookieが存在しない場合の検証。
     * デフォルトのフロントエンドパス（/）へリダイレクトされることを確認します。
     */
    @Test
    @DisplayName("認証成功時: Cookieなしならデフォルトパス / へリダイレクトすること")
    fun onAuthenticationSuccess_withoutRedirectCookie_redirectsToDefaultPath() {
        // Arrange: Cookieなしの通常リクエストを作成
        val request = MockServerHttpRequest.get("/oauth2/authorization/keycloak").build()
        val exchange = MockServerWebExchange.from(request)
        val chain = Mockito.mock(WebFilterChain::class.java)
        val webFilterExchange = WebFilterExchange(exchange, chain)
        val authentication = Mockito.mock(Authentication::class.java)

        // Act: 成功ハンドラを実行
        val result = handler.onAuthenticationSuccess(webFilterExchange, authentication)

        // Assert: デフォルトパス（/）へのリダイレクトを検証
        StepVerifier.create(result).verifyComplete()

        val response = exchange.response
        assertThat(response.statusCode).isEqualTo(HttpStatus.FOUND)
        assertThat(response.headers.location.toString()).isEqualTo("http://localhost:3000/")
    }
}