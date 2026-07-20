package com.example.bff.integration.client

import com.example.bff.integration.config.AppProperties
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

/**
 * Keycloak の RP-Initiated Logout をバックチャネルで実行するクライアント.
 *
 * BFF が POST /logout を受信した際、Webブラウザへのリダイレクトに依存せずに
 * サーバーサイドから直接 Keycloak の end_session_endpoint へログアウト要求を送信し、
 * Keycloak 上のセッションを確実に破棄することを目的とする.
 *
 * @property appProperties アプリケーション設定（Keycloak ログアウト URL 等）
 */
@Component
class KeycloakLogoutClient(
    private val appProperties: AppProperties,
) {
    @Autowired
    @Qualifier("keycloakWebClient")
    private lateinit var webClient: WebClient
    private val log = LoggerFactory.getLogger(KeycloakLogoutClient::class.java)

    /**
     * Keycloak に対して RP-Initiated Logout を実行する.
     *
     * @param idToken IDトークン（`id_token_hint` として送信）
     * @param postLogoutRedirectUri ログアウト後に Keycloak がリダイレクトする先の URI
     * @return 完了を通知する [Mono]
     */
    fun logout(
        idToken: String?,
        postLogoutRedirectUri: String,
    ): Mono<Void> {
        val requestBody = mutableMapOf<String, String?>(
            "post_logout_redirect_uri" to postLogoutRedirectUri,
        )

        if (!idToken.isNullOrBlank()) {
            requestBody["id_token_hint"] = idToken
        }

        val baseUrl = appProperties.keycloakLogoutUrl

        log.info(
            "KeycloakLogoutClient.logout start. keycloakLogoutUrl={} postLogoutRedirectUri={} hasIdToken={}",
            baseUrl,
            postLogoutRedirectUri,
            idToken != null,
        )

        return webClient
            .post()
            .uri(baseUrl)
            .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
            .bodyValue(requestBody)
            .retrieve()
            .onStatus({ status -> status.isError }) { response ->
                log.error(
                    "KeycloakLogoutClient.logout failed. status={}",
                    response.statusCode(),
                )
                Mono.error(RuntimeException("Keycloak logout failed: ${response.statusCode()}"))
            }
            .bodyToMono(Void::class.java)
            .doOnSuccess { log.info("KeycloakLogoutClient.logout complete.") }
            .doOnError { e ->
                log.error(
                    "KeycloakLogoutClient.logout error.",
                    e,
                )
    }
}
}