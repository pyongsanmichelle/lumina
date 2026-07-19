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
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource

/**
 * BFF（Backend For Frontend）のセキュリティ設定クラス。
 *
 * Spring SecurityのWebFlux設定を統括し、認証・認可、CORS、CSRF対策、
 * および各カスタムハンドラの紐付けを行います。
 *
 * @property appProperties アプリケーション設定（フロントエンドのオリジン情報などを提供）
 * @property customLogoutSuccessHandler ログアウト時のカスタム成功ハンドラ
 * @property customAuthenticationSuccessHandler ログイン時のカスタム成功ハンドラ
 */
@Configuration
@EnableWebFluxSecurity
class SecurityConfig(
    private val appProperties: AppProperties,
    private val customLogoutSuccessHandler: CustomLogoutSuccessHandler,
    private val customAuthenticationSuccessHandler: CustomAuthenticationSuccessHandler,
) {
    /**
     * セキュリティフィルターチェーンを構築します。
     *
     * 本設定により、以下のセキュリティ層が適用されます。
     * 1. OAuth2 認証フロー
     * 2. ログアウト処理
     * 3. CSRF/CORS 対策
     * 4. 認証失敗時のエラー制御
     * 5. エンドポイントごとの認可ルール
     *
     * @param http Spring Securityのビルダークラス
     * @return 構成済みのフィルターチェーン
     */
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        http
            // OAuth2 ログインの設定
            .oauth2Login { oauth2 ->
                // 認証成功時、Cookieから保存しておいたリダイレクト先に遷移させるカスタムハンドラ
                oauth2.authenticationSuccessHandler(customAuthenticationSuccessHandler)
            }
            // ログアウトの設定
            .logout { logout ->
                // サーバー側の認証コンテキスト（セッション等）を破棄
                logout.logoutHandler(SecurityContextServerLogoutHandler())
                // Keycloak側でもログアウトを実行するカスタムハンドラ
                logout.logoutSuccessHandler(customLogoutSuccessHandler)
            }
            // CSRF 対策の設定
            .csrf { csrf ->
                // SPAから参照できるよう、HttpOnly=false でクッキーにトークンを保存
                csrf.csrfTokenRepository(
                    CookieServerCsrfTokenRepository.withHttpOnlyFalse().apply {
                        setCookiePath("/")
                    },
                )
            }
            // CORS 設定
            .cors { cors ->
                cors.configurationSource(corsConfigurationSource())
            }
            // 例外処理の設定（認証エラー時の対応）
            .exceptionHandling { exceptionHandling ->
                // 未認証アクセス時にログイン画面へリダイレクトせず、401 JSONを返す
                exceptionHandling.authenticationEntryPoint(CustomAuthenticationEntryPoint())
            }
            // アクセス制御ルールの設定
            .authorizeExchange { authorize ->
                // ヘルスチェック、認証エンドポイント、ログイン・ログアウト関連は認証不要
                authorize.pathMatchers("/health", "/oauth2/**", "/login/**", "/logout").permitAll()
                // 上記以外はすべて認証が必要
                authorize.anyExchange().authenticated()
            }
            // 認証処理の直前でリダイレクトパスをCookieに保存するカスタムフィルターを挿入
            .addFilterBefore(
                RedirectUriCookieFilter(appProperties),
                SecurityWebFiltersOrder.AUTHENTICATION,
            ).build()

    /**
     * CORSの設定を構築します。
     * フロントエンド（SPA）からのクレデンシャル付きリクエスト（Cookie等の送信）を許可します。
     *
     * @return 設定済みのCORSソース
     */
    private fun corsConfigurationSource(): CorsConfigurationSource {
        val configuration =
            CorsConfiguration().apply {
                allowedOrigins = listOf(appProperties.frontendOrigin) // フロントエンドオリジンを許可
                allowCredentials = true // 認証情報（Cookie等）の送信を許可
                allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
                allowedHeaders = listOf("Content-Type", "Authorization", "X-XSRF-TOKEN")
            }

        return UrlBasedCorsConfigurationSource().apply {
            registerCorsConfiguration("/**", configuration)
        }
    }
}
