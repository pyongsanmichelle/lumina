package com.example.bff.presentation.config

import com.example.bff.integration.client.KeycloakLogoutClient
import com.example.bff.integration.config.AppProperties
import org.slf4j.LoggerFactory
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
    private val keycloakLogoutClient: KeycloakLogoutClient,
) : ServerLogoutSuccessHandler {
    private val log = LoggerFactory.getLogger(CustomLogoutSuccessHandler::class.java)

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
        val traceId = org.slf4j.MDC.get("trace_id") ?: "no-trace-id"
        val userId = org.slf4j.MDC.get("user_id") ?: "anonymous"

        log.info(
            "CustomLogoutSuccessHandler.onLogoutSuccess start. traceId={} userId={} authenticationPrincipal={}",
            traceId,
            userId,
            authentication.name,
        )

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

        // バックチャネルで Keycloak のログアウトを実行し、完了後にフロントエンドへリダイレクト
        return keycloakLogoutClient.logout(idToken, redirectUri)
            .doOnSuccess {
                log.info(
                    "CustomLogoutSuccessHandler.onLogoutSuccess Keycloak logout success. traceId={}",
                    traceId,
                )
            }
            .doOnError { e ->
                log.warn(
                    "CustomLogoutSuccessHandler.onLogoutSuccess Keycloak logout failed. traceId={} error={}",
                    traceId,
                    e.message,
                )
            }
            .then(
                Mono.defer {
                    val redirectLocation =
                        UriComponentsBuilder
                            .fromUriString(appProperties.keycloakLogoutUrl)
                            .queryParam("post_logout_redirect_uri", redirectUri)
                            .apply {
                                if (idToken != null) {
                                    queryParam("id_token_hint", idToken)
                                }
                            }.build()
                            .toUri()

                    val response = webFilterExchange.exchange.response
                    response.statusCode = HttpStatus.FOUND
                    response.headers.location = redirectLocation

                    log.info(
                        "CustomLogoutSuccessHandler.onLogoutSuccess redirect. traceId={} redirectLocation={}",
                        traceId,
                        redirectLocation,
                    )

                    response.setComplete()
                }
            )
    }
}
