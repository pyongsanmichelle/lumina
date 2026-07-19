package com.example.bff.integration.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.ReactiveOAuth2AuthorizedClientProviderBuilder
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.DefaultReactiveOAuth2AuthorizedClientManager
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository
import org.springframework.security.oauth2.client.web.reactive.function.client.ServerOAuth2AuthorizedClientExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient

/**
 * 外部API通信用の [WebClient] Bean を定義する構成クラス。
 *
 * アプリケーション設定に基づき、標準的な HTTP 通信クライアントを構築します。
 * 設定値は [AppProperties] を介して一元管理されます。
 *
 * また、Token Relay 方式により、ログインユーザーのアクセストークンを
 * `Authorization: Bearer` ヘッダとして API サーバーへ引き継ぎます。
 * これによりブラウザにトークンを露出させずに api/ の認証を通過させます。
 *
 * @property appProperties アプリケーションの構成プロパティ（APIのベースURL等を保持）
 */
@Configuration
class WebClientConfig(
    private val appProperties: AppProperties
) {

    /**
     * ログイン済みクライアントのアクセストークンを管理する
     * [ReactiveOAuth2AuthorizedClientManager] を構築します。
     */
    @Bean
    fun authorizedClientManager(
        clientRegistrationRepository: ReactiveClientRegistrationRepository,
        authorizedClientRepository: ServerOAuth2AuthorizedClientRepository
    ): ReactiveOAuth2AuthorizedClientManager {
        val authorizedClientProvider = ReactiveOAuth2AuthorizedClientProviderBuilder.builder()
            .authorizationCode()
            .refreshToken()
            .build()
        return DefaultReactiveOAuth2AuthorizedClientManager(
            clientRegistrationRepository,
            authorizedClientRepository
        ).apply {
            setAuthorizedClientProvider(authorizedClientProvider)
        }
    }

    /**
     * API通信用の [WebClient] インスタンスを生成して Bean として登録します。
     *
     * Token Relay 用の [ServerOAuth2AuthorizedClientExchangeFilterFunction] を組み込み、
     * 現在ログイン中のユーザーのアクセストークンを自動的に付与します。
     *
     * @param builder Spring Bootが自動構成した WebClient.Builder
     * @param authorizedClientManager 認可済みクライアント管理コンポーネント
     * @return 構築済みの [WebClient] インスタンス
     */
    @Bean
    fun apiWebClient(
        builder: WebClient.Builder,
        authorizedClientManager: ReactiveOAuth2AuthorizedClientManager
    ): WebClient {
        val oauth2Filter = ServerOAuth2AuthorizedClientExchangeFilterFunction(authorizedClientManager)
        // 現在ログイン中のユーザーの認可済みクライアント（アクセストークン）を利用する
        oauth2Filter.setDefaultOAuth2AuthorizedClient(true)
        return builder
            .baseUrl(appProperties.apiBaseUrl)
            .filter(oauth2Filter)
            .build()
    }
}
