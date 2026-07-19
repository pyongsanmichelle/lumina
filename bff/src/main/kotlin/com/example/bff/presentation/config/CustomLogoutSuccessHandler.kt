package com.example.bff.presentation.config

import com.example.bff.integration.config.AppProperties
import org.springframework.http.HttpStatus
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

/**
 * カスタムログアウト成功ハンドラ (RP-Initiated Logout対応)。
 *
 * BFFアプリケーションでのログアウト処理が完了した後に実行されます。
 * Keycloak（OpenID Provider）側のセッションも確実に破棄させるため、
 * Keycloakの `end_session_endpoint` へリダイレクトするレスポンスを生成します。
 *
 * @property appProperties アプリケーション設定（KeycloakのログアウトURLやフロントエンドのオリジンを保持）
 */
@Component
class CustomLogoutSuccessHandler(
    private val appProperties: AppProperties,
) : ServerLogoutSuccessHandler {
    /**
     * ログアウト成功時のリダイレクト処理を実行します。
     *
     * @param webFilterExchange WebFilterの実行コンテキスト（リクエストやレスポンスへのアクセスを提供）
     * @param authentication ログアウトしたユーザーの認証情報
     * @return 処理の完了を通知するMono<Void>
     */
    override fun onLogoutSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication,
    ): Mono<Void> {
        // Keycloakでのログアウト完了後に戻ってくる、フロントエンドのURLを定義
        val redirectUri = "${appProperties.frontendOrigin}/"

        // 認証情報から IDトークン (id_token_hint) を抽出
        // どのセッションを終了させるかをKeycloakに伝えるために必要（無い場合はnullになる）
        val idToken =
            (authentication as? OAuth2AuthenticationToken)
                ?.let { token ->
                    val oidcUser = token.principal as? OidcUser
                    oidcUser?.idToken?.tokenValue
                }

        // UriComponentsBuilder を用いて、KeycloakへのリダイレクトURLを安全に構築
        val redirectLocation =
            UriComponentsBuilder
                .fromUriString(appProperties.keycloakLogoutUrl)
                // ログアウト後の遷移先URLを指定（値は自動的にURLエンコードされる）
                .queryParam("post_logout_redirect_uri", redirectUri)
                .apply {
                    // IDトークンが存在する場合のみ、クエリパラメータとして追加する
                    if (idToken != null) {
                        queryParam("id_token_hint", idToken)
                    }
                }.build()
                // 文字列ではなく、レスポンスヘッダに設定できる java.net.URI オブジェクトとして出力
                .toUri()

        // クライアントに対するリダイレクトレスポンスの設定
        val response = webFilterExchange.exchange.response
        // HTTPステータス 302 (Found) を設定
        response.statusCode = HttpStatus.FOUND
        // Locationヘッダに構築したKeycloakのログアウトエンドポイントを設定
        response.headers.location = redirectLocation

        // レスポンス処理の完了をReactorの非同期チェーンに通知
        return response.setComplete()
    }
}
