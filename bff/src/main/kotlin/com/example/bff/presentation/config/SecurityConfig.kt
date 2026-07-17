package com.example.bff.presentation.config

import com.example.bff.integration.config.AppProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource

/**
 * BFFアプリケーションのセキュリティ設定を行う構成クラス。
 * Spring WebFlux用のSpring Security設定を定義します。
 *
 * @property appProperties アプリケーションのプロパティ設定（CORSのフロントエンドオリジン取得などに使用）
 * @property customLogoutSuccessHandler ログアウト成功時のカスタム処理を行うハンドラー
 */
@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val appProperties: AppProperties,
    private val customLogoutSuccessHandler: CustomLogoutSuccessHandler
) {

    /**
     * セキュリティWebフィルターチェーンを構成します。
     *
     * 以下のセキュリティ設定を適用します：
     * - **OAuth2ログイン**: OAuth2を利用したログインを有効化
     * - **ログアウト**: セッション破棄およびカスタムログアウトハンドラーの設定
     * - **CSRF**: SPA（Single Page Application）向けに `HttpOnly=false` のCookieとしてCSRFトークンを送信
     * - **CORS**: フロントエンドオリジンからのクレデンシャル付きリクエストを許可する設定
     * - **例外ハンドリング**: 未認証時のアクセスに対するカスタムエントリーポイントの設定
     * - **アクセス認可**: `/health` やログイン/ログアウト関連のエンドポイントを公開し、その他を要認証に設定
     * - **カスタムフィルター**: 認証処理の前に、リダイレクトURIをCookieで管理する [RedirectUriCookieFilter] を挿入
     *
     * @param http HTTPセキュリティ構成用の [ServerHttpSecurity] ビルダー
     * @return 構築された [SecurityWebFilterChain] オブジェクト
     */
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        // ログイン完了後にフロントエンドのどの画面に戻すか（リダイレクト先）を保持するためのフィルター
        val redirectFilter = RedirectUriCookieFilter(appProperties)

        return http
            // OAuth2 / OpenID Connectでのログイン処理を有効化
            .oauth2Login { }
            // ログアウト処理の設定
            .logout { logout ->
                // サーバー側のセッション等の認証情報を破棄
                logout.logoutHandler(SecurityContextServerLogoutHandler())
                // ログアウト成功時の事後処理（Cookie削除や特定ページへのリダイレクトなど）
                logout.logoutSuccessHandler(customLogoutSuccessHandler)
            }
            // CSRF（クロスサイトリクエストフォージェリ）対策
            .csrf { csrf ->
                csrf.csrfTokenRepository(
                    // SPAからの通信の場合、JavaScriptでCSRFトークンを読み取ってヘッダーにセットし直す必要があるため、
                    // あえて HttpOnly=false に設定してCookieを発行する
                    CookieServerCsrfTokenRepository.withHttpOnlyFalse().apply {
                        setCookiePath("/")
                    }
                )
            }
            // CORS（クロスオリジンリソース共有）設定
            // フロントエンドとBFFのドメイン/ポートが異なる場合の通信ブロックを解除する
            .cors { cors ->
                cors.configurationSource(CorsConfigurationSource { _ ->
                    CorsConfiguration().apply {
                        addAllowedOrigin(appProperties.frontendOrigin) // フロントエンドのURLを許可
                        allowCredentials = true // 認証情報やCSRFトークンを含むCookieの送信を許可
                        addAllowedMethod("GET")
                        addAllowedMethod("POST")
                        addAllowedMethod("PUT")
                        addAllowedMethod("DELETE")
                        addAllowedMethod("OPTIONS")
                        addAllowedHeader("Content-Type")
                        addAllowedHeader("Authorization")
                        addAllowedHeader("X-XSRF-TOKEN")
                    }
                })
            }
            // 例外ハンドリング（未認証時の対応）
            .exceptionHandling { exceptionHandling ->
                // デフォルトの「ログイン画面（HTML）へのリダイレクト」を防ぎ、
                // API通信に適した 401 Unauthorized エラー等を返すように上書き
                exceptionHandling.authenticationEntryPoint(CustomAuthenticationEntryPoint())
            }
            // エンドポイントのアクセス認可ルールの定義（上の設定ほど優先される）
            .authorizeExchange { authorize ->
                authorize.pathMatchers("/auth/me").authenticated()
                authorize.pathMatchers("/health").permitAll()
                authorize.pathMatchers("/oauth2/**").permitAll()
                authorize.pathMatchers("/login/**").permitAll()
                authorize.pathMatchers("/logout").permitAll()
                // 上記で指定されていないすべての通信はログイン（認証）を必須とする
                authorize.anyExchange().authenticated()
            }
            // 実際の認証処理が始まる前に、リダイレクト先を保存するカスタムフィルターを挿入
            .addFilterBefore(redirectFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }
}