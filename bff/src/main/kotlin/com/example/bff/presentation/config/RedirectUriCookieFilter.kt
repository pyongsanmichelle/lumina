package com.example.bff.presentation.config

import com.example.bff.integration.config.AppProperties
import org.springframework.http.HttpCookie
import org.springframework.http.ResponseCookie
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.time.Duration

/**
 * ディープリンク復帰用のフィルター。
 * `/oauth2/authorization/keycloak?redirect_uri=...` のクエリパラメータを読み取り、
 * 認証成功後のリダイレクト先を一時Cookie (`POST_LOGIN_REDIRECT_URI`) に保存する。
 *
 * このフィルターは `SecurityWebFiltersOrder.AUTHENTICATION` より前に実行されるよう
 * SecurityConfig 内で addFilterBefore() により登録される。
 * これにより、標準の OAuth2AuthorizationRequestRedirectWebFilter より先に処理される。
 */
class RedirectUriCookieFilter(
    private val appProperties: AppProperties
) : WebFilter {

    companion object {
        const val REDIRECT_URI_PARAM = "redirect_uri"
        const val POST_LOGIN_REDIRECT_URI_COOKIE = "POST_LOGIN_REDIRECT_URI"
        const val MAX_AGE_MINUTES = 5L
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        val path = request.path.pathWithinApplication().value()

        // `/oauth2/authorization/keycloak` へのリクエストのみ処理
        if (!path.startsWith("/oauth2/authorization/keycloak")) {
            return chain.filter(exchange)
        }

        val redirectUri = request.queryParams.getFirst(REDIRECT_URI_PARAM)
            ?: return chain.filter(exchange)

        // オープンリダイレクト対策：redirect_uri のバリデーション
        val validatedPath = validateRedirectUri(redirectUri)

        if (validatedPath != null) {
            // 検証成功：一時Cookieに保存
            val cookie = ResponseCookie.from(POST_LOGIN_REDIRECT_URI_COOKIE, validatedPath)
                .httpOnly(true)
                .secure(appProperties.frontendOrigin.startsWith("https"))
                .sameSite("Lax")
                .path("/")
                .maxAge(Duration.ofMinutes(MAX_AGE_MINUTES))
                .build()

            exchange.response.addCookie(cookie)
        }
        // 検証失敗時は Cookie を設定せず、そのまま標準のOAuth2ログインフローを継続。
        // 結果として CustomAuthenticationSuccessHandler が Cookie 不在を検知し、
        // デフォルトの FRONTEND_ORIGIN/ へ誘導する。

        return chain.filter(exchange)
    }

    /**
     * redirect_uri を検証する。
     * 以下の条件をすべて満たせば検証成功としてパス部分を返す。
     * 1. `/` から始まる相対パスであること
     * 2. `//`（プロトコル相対URL）で始まらないこと
     * 3. `http://` または `https://` を含まないこと
     * 4. `\`（バックスラッシュ）を含まないこと
     * 5. 制御文字を含まないこと
     *
     * @return 検証成功時はパス (/dashboard 等)、失敗時は null
     */
    private fun validateRedirectUri(redirectUri: String): String? {
        // 条件5: 制御文字を含まない
        if (redirectUri.any { it.code in 0..31 || it.code == 127 }) {
            return null
        }

        // 条件4: バックスラッシュを含まない
        if (redirectUri.contains("\\")) {
            return null
        }

        // 条件1: `/` から始まる
        if (!redirectUri.startsWith("/")) {
            return null
        }

        // 条件2: `//`（プロトコル相対URL）で始まらない
        if (redirectUri.startsWith("//")) {
            return null
        }

        // 条件3: `http://` または `https://` を含まない
        if (redirectUri.contains("http://") || redirectUri.contains("https://")) {
            return null
        }

        return redirectUri
    }
}