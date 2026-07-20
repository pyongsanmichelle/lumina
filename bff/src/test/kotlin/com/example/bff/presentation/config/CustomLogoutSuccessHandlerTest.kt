package com.example.bff.presentation.config

import com.example.bff.integration.config.AppProperties
import org.assertj.core.api.Assertions.assertThat
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
import org.springframework.web.util.UriComponentsBuilder
import reactor.test.StepVerifier
import java.net.URLEncoder
import java.nio.charset.StandardCharsets

/**
 * [CustomLogoutSuccessHandler] のテストクラス。
 *
 * ログアウト成功時のリダイレクト処理において、IDトークンのヒント付与や
 * ログアウト後のリダイレクトURIが正しく構築されるかを検証します。
 */
class CustomLogoutSuccessHandlerTest {
    private val appProperties =
        AppProperties(
            frontendOrigin = "http://localhost:3000",
            bffOrigin = "http://localhost:8080",
            apiBaseUrl = "http://api:8080",
            keycloakLogoutUrl = "http://keycloak:8080/realms/lumina/protocol/openid-connect/logout",
        )

    private val handler = CustomLogoutSuccessHandler(appProperties)

    /**
     * 認証情報に OidcUser が含まれる場合、IDトークンをヒントとして
     * ログアウトURLに付与することを確認します。
     */
    @Test
    @DisplayName("ログアウト成功時: idTokenありなら Location に id_token_hint と post_logout_redirect_uri が含まれること")
    fun onLogoutSuccess_withIdToken_includesIdTokenHintInLocation() {
        // Arrange: モック環境の構築
        val request = MockServerHttpRequest.get("/logout").build()
        val exchange = MockServerWebExchange.from(request)
        val chain = Mockito.mock(WebFilterChain::class.java)
        val webFilterExchange = WebFilterExchange(exchange, chain)

        // OIDC ユーザーと ID トークンのモックを作成
        val oidcUser = Mockito.mock(OidcUser::class.java)
        val idToken = Mockito.mock(OidcIdToken::class.java)
        Mockito.`when`(idToken.tokenValue).thenReturn("dummy-id-token")
        Mockito.`when`(oidcUser.idToken).thenReturn(idToken)

        val authentication = Mockito.mock(OAuth2AuthenticationToken::class.java)
        Mockito.`when`(authentication.principal).thenReturn(oidcUser)

        // Act: ログアウトハンドラーの実行
        val result = handler.onLogoutSuccess(webFilterExchange, authentication)

        // Assert: 結果の検証
        StepVerifier.create(result).verifyComplete()

        val response = exchange.response
        assertThat(response.statusCode).isEqualTo(HttpStatus.FOUND)

        val location = response.headers.location.toString()
        val uri = UriComponentsBuilder.fromUriString(location).build()

        // 検証：期待値はプロパティから組み立てる
        val expectedRedirectUri = "${appProperties.frontendOrigin}/"

        assertThat(uri.queryParams.getFirst("id_token_hint")).isEqualTo("dummy-id-token")
        assertThat(uri.queryParams.getFirst("post_logout_redirect_uri")).isEqualTo(expectedRedirectUri)
    }

    /**
     * OIDC ユーザーではない場合、IDトークンヒントは付与せず、
     * ログアウト後のリダイレクトURIのみが付与されることを確認します。
     */
    @Test
    @DisplayName("ログアウト成功時: idTokenなしなら Location に id_token_hint が含まれないこと")
    fun onLogoutSuccess_withoutIdToken_doesNotIncludeIdTokenHintInLocation() {
        // Arrange: 通常の Authentication モックを作成
        val request = MockServerHttpRequest.get("/logout").build()
        val exchange = MockServerWebExchange.from(request)
        val chain = Mockito.mock(WebFilterChain::class.java)
        val webFilterExchange = WebFilterExchange(exchange, chain)
        val authentication = Mockito.mock(Authentication::class.java)

        // Act
        val result = handler.onLogoutSuccess(webFilterExchange, authentication)

        // Assert
        StepVerifier.create(result).verifyComplete()

        val response = exchange.response
        assertThat(response.statusCode).isEqualTo(HttpStatus.FOUND)

        val location = response.headers.location.toString()

        // 実際に生成された URL を出力
        println("DEBUG (Without ID Token): $location")

        val uri = UriComponentsBuilder.fromUriString(location).build()

        // IDトークンヒントが含まれていないこと
        assertThat(uri.queryParams.containsKey("id_token_hint")).isFalse()

        // ログアウト後のリダイレクトURIは含まれているか
        val expectedRedirectUri = "${appProperties.frontendOrigin}/"
        assertThat(uri.queryParams.getFirst("post_logout_redirect_uri")).isEqualTo(expectedRedirectUri)
    }
}
