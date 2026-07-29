package com.example.bff.presentation.config

import com.example.bff.integration.config.AppProperties
import kotlinx.coroutines.reactor.awaitSingleOrNull
import kotlinx.coroutines.reactor.mono
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.logout.ServerLogoutSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono
import java.time.Duration

/**
 * カスタムログアウト成功ハンドラ (RP-Initiated Logout対応)。
 *
 * BFFアプリケーションでのログアウト処理が完了した後に実行されます。
 * Kotlin Coroutines（monoビルダー）を利用し、非同期処理を同期的に記述して可読性を高めています。
 *
 * @property appProperties アプリケーション設定（KeycloakのログアウトURLやフロントエンドのオリジンを保持）
 * @property clientRegistrationRepository OAuth2のクライアント設定（client_id等）を非同期で取得するためのリポジトリ
 */
@Component
class CustomLogoutSuccessHandler(
    private val appProperties: AppProperties,
    private val clientRegistrationRepository: ReactiveClientRegistrationRepository,
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
    ): Mono<Void> =
        mono {
            // ログ出力用のMDC情報取得
            val traceId = org.slf4j.MDC.get("trace_id") ?: "no-trace-id"
            val userId = org.slf4j.MDC.get("user_id") ?: "anonymous"

            log.info(
                "CustomLogoutSuccessHandler.onLogoutSuccess start. traceId={} userId={} authenticationPrincipal={}",
                traceId,
                userId,
                authentication.name,
            )

            // 認証情報から OIDC の IDトークン (id_token_hint) を安全に抽出
            val idToken =
                (authentication as? OAuth2AuthenticationToken)
                    ?.let { token -> (token.principal as? OidcUser)?.idToken?.tokenValue }

            if (idToken == null) {
                // id_token_hint が無いと Keycloak 側でサイレントログアウトに失敗する可能性があるため警告を残す
                log.warn("id_token_hint is missing. traceId={} Keycloak may not silently terminate the SSO session.", traceId)
            }

            // ClientRegistrationの取得 (ここで awaitSingleOrNull() を使い、Monoを解除して直接値を受け取る)
            // 非同期アクセスですが、スレッドをブロックせずに処理の完了を待機します。
            val registration =
                clientRegistrationRepository.findByRegistrationId("keycloak").awaitSingleOrNull()
                    ?: throw IllegalStateException("OAuth2 client registration 'keycloak' is not found. Check application.yaml")

            // registrationが取得できなかった場合のフォールバック値を設定
            val clientId = registration.clientId

            // リダイレクト先URLの構築
            val redirectUri = "${appProperties.frontendOrigin}/"
            val redirectLocation =
                UriComponentsBuilder
                    .fromUriString(appProperties.keycloakLogoutUrl)
                    .queryParam("post_logout_redirect_uri", redirectUri)
                    .queryParam("client_id", clientId)
                    .apply {
                        if (idToken != null) {
                            queryParam("id_token_hint", idToken)
                        }
                    }.encode() // パラメータ（JWTやURL）を確実にパーセントエンコードする
                    .build()
                    .toUri()

            // クッキーの削除設定
            val response = webFilterExchange.exchange.response

            // SESSION Cookie 削除用
            val removeSessionCookie =
                ResponseCookie
                    .from("SESSION", "")
                    .maxAge(Duration.ZERO)
                    .path("/")
                    .httpOnly(true)
                    .sameSite("Lax")
                    .build()

            // XSRF-TOKEN Cookie 削除用
            val removeXsrfCookie =
                ResponseCookie
                    .from("XSRF-TOKEN", "")
                    .maxAge(Duration.ZERO)
                    .path("/")
                    .httpOnly(false)
                    .build()

            response.addCookie(removeSessionCookie)
            response.addCookie(removeXsrfCookie)

            // HTTPステータスとLocationヘッダーのセット (302リダイレクト)
            response.statusCode = HttpStatus.FOUND
            response.headers.location = redirectLocation

            log.info(
                "CustomLogoutSuccessHandler.onLogoutSuccess redirect. traceId={} redirectLocation={}",
                traceId,
                redirectLocation,
            )

            // レスポンス処理の完了を待機して終了
            response.setComplete().awaitSingleOrNull()

            // mono<Void> の戻り値の型合わせのため null を返す
            return@mono null
        }
}
