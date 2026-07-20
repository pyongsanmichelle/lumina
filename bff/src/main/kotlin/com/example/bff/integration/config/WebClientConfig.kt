package com.example.bff.integration.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

/**
 * 外部API通信用の [WebClient] Bean を定義する構成クラス。
 *
 * アプリケーション設定に基づき、標準的な HTTP 通信クライアントを構築します。
 * 設定値は [AppProperties] を介して一元管理されます。
 *
 * @property appProperties アプリケーションの構成プロパティ（APIのベースURL等を保持）
 */
@Configuration
class WebClientConfig(
    private val appProperties: AppProperties,
) {
    /**
     * API通信用の [WebClient] インスタンスを生成して Bean として登録します。
     *
     * Spring Boot が提供する [WebClient.Builder] を注入することで、
     * 共通の設定やフィルタリングを適用しやすい柔軟な構成にしています。
     *
     * @param builder Spring Bootが自動構成した WebClient.Builder
     * @return 構築済みの [WebClient] インスタンス
     */
    @Bean
    fun apiWebClient(builder: WebClient.Builder): WebClient =
        builder
            .baseUrl(appProperties.apiBaseUrl)
            .build()

    /**
     * Keycloak との通信用 [WebClient].
     *
     * RP-Initiated Logout のバックチャネル呼び出し等に使用する.
     */
    @Bean
    fun keycloakWebClient(builder: WebClient.Builder): WebClient =
        builder.build()
}
