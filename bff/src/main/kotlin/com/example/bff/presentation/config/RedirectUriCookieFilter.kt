package com.example.bff.presentation.config

import com.example.bff.integration.config.AppProperties
import org.springframework.http.ResponseCookie
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.time.Duration

/**
 * ディープリンク復帰用のフィルター。
 * 
 * `/oauth2/authorization/keycloak?redirect_uri=...` のクエリパラメータを読み取り、
 * 認証成功後のリダイレクト先パスを一時Cookie (`POST_LOGIN_REDIRECT_URI`) に保存します。
 *
 * このフィルターは `SecurityWebFiltersOrder.AUTHENTICATION` より前に実行されるよう
 * SecurityConfig 内で `addFilterBefore()` により登録されます。
 * これにより、Spring Security 標準の OAuth2 ログインリダイレクト処理より先に実行されます。
 *
 * @property appProperties アプリケーション設定（CookieのSecure属性判定等に使用）
 */
class RedirectUriCookieFilter(
    private val appProperties: AppProperties
) : WebFilter {

    companion object {
        /** 復帰先パスを受け取るためのクエリパラメータ名 */
        const val REDIRECT_URI_PARAM = "redirect_uri"
        
        /** 復帰先パスを保持するためのCookie名 */
        const val POST_LOGIN_REDIRECT_URI_COOKIE = "POST_LOGIN_REDIRECT_URI"
        
        /** Cookieの有効期限（分）。ログイン操作にかかる時間を考慮して5分間とする */
        const val MAX_AGE_MINUTES = 5L
    }

    /**
     * HTTPリクエストをインターセプトし、特定のログイン開始URLに対する処理を差し込みます。
     * 
     * リクエストパスが Keycloak の認可エンドポイントへのアクセスであり、
     * かつ `redirect_uri` パラメータが存在する場合にのみ、その値を検証してCookieに保存します。
     *
     * @param exchange 現在のHTTPリクエストおよびレスポンスのコンテキスト
     * @param chain フィルターチェーン（後続のフィルターやハンドラを呼び出すために使用）
     * @return フィルター処理の完了を示す `Mono<Void>`
     */
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        val path = request.path.pathWithinApplication().value()

        // 対象パスの絞り込み: `/oauth2/authorization/keycloak` へのリクエストのみ処理
        if (!path.startsWith("/oauth2/authorization/keycloak")) {
            return chain.filter(exchange)
        }

        // パラメータの取得: クエリパラメータに redirect_uri がなければ何もしない
        val redirectUri = request.queryParams.getFirst(REDIRECT_URI_PARAM)
            ?: return chain.filter(exchange)

        // オープンリダイレクト対策: redirect_uri が安全な相対パスかバリデーション
        val validatedPath = validateRedirectUri(redirectUri)

        if (validatedPath != null) {
            // 検証成功時: 一時Cookieを発行してパスを保存
            val cookie = ResponseCookie.from(POST_LOGIN_REDIRECT_URI_COOKIE, validatedPath)
                .httpOnly(true) // JavaScriptからのアクセスを禁止（XSS対策）
                .secure(appProperties.frontendOrigin.startsWith("https")) // HTTPS環境ならSecure属性を付与
                .sameSite("Lax") // 別サイトからの遷移時にもCookieを送信させる
                .path("/")
                .maxAge(Duration.ofMinutes(MAX_AGE_MINUTES)) // ログインにかかる時間を考慮し5分間有効
                .build()

            exchange.response.addCookie(cookie)
        }
        
        // 検証失敗時は Cookie を設定せず、そのまま標準のOAuth2ログインフローを継続。
        // 結果として CustomAuthenticationSuccessHandler が Cookie 不在を検知し、
        // デフォルトの FRONTEND_ORIGIN/ へ誘導する安全なフォールバックとなります。
        return chain.filter(exchange)
    }

    /**
     * `redirect_uri` が安全な同一オリジン内のパスであるかを検証します（オープンリダイレクト対策）。
     *
     * 以下の条件をすべて満たす場合のみ安全と判定します：
     * 1. 空文字ではないこと
     * 2. 制御文字を含まないこと (HTTPレスポンス分割攻撃等の防止)
     * 3. `\`（バックスラッシュ）を含まないこと (ブラウザの誤解釈を悪用した攻撃の防止)
     * 4. `/` から始まる相対パスであること
     * 5. `//`（プロトコル相対URL）で始まらないこと (外部ドメインへの誘導防止)
     * 6. `http://` または `https://` を含まないこと
     *
     * @param redirectUri 検証対象のURI文字列
     * @return 安全と判定された場合はパス文字列、危険・不正な場合は `null`
     */
    private fun validateRedirectUri(redirectUri: String): String? {
        // 条件1: 空文字や空白のみの場合はNG
        if (redirectUri.isBlank()) return null

        // 条件2: 制御文字が含まれている場合はNG
        if (redirectUri.any { it.code in 0..31 || it.code == 127 }) return null

        // 条件3: バックスラッシュが含まれている場合はNG
        if (redirectUri.contains("\\")) return null

        // 条件4: 必ず `/` から始まる相対パスでなければならない
        if (!redirectUri.startsWith("/")) return null

        // 条件5: `//` で始まってはならない
        if (redirectUri.startsWith("//")) return null

        // 条件6: 万が一スキームが含まれている場合はNG
        if (redirectUri.contains("http://") || redirectUri.contains("https://")) return null

        return redirectUri
    }
}