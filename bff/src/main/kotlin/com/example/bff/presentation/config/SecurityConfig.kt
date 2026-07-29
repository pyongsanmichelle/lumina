package com.example.bff.presentation.config

import com.example.bff.integration.config.AppProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.logout.DelegatingServerLogoutHandler
import org.springframework.security.web.server.authentication.logout.SecurityContextServerLogoutHandler
import org.springframework.security.web.server.authentication.logout.WebSessionServerLogoutHandler
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository
import org.springframework.security.web.server.csrf.CsrfToken
import org.springframework.security.web.server.csrf.ServerCsrfTokenRequestAttributeHandler
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.reactive.CorsConfigurationSource
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

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
    private val clientRegistrationRepository: ReactiveClientRegistrationRepository,
) {
    /**
     * セキュリティフィルターチェーンを構築します。
     *
     * @param http Spring Securityのビルダークラス
     * @return 構成済みのフィルターチェーン
     */
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain =
        http
            // OAuth2 ログインの設定
            .oauth2Login { oauth2 ->
                // 認可開始エンドポイント
                oauth2.authorizationRequestResolver(
                    DefaultServerOAuth2AuthorizationRequestResolver(
                        clientRegistrationRepository,
                        PathPatternParserServerWebExchangeMatcher("/oauth2/authorization/{registrationId}"),
                    ),
                )
                // コールバック(redirect_uri)の受け口
                oauth2.authenticationMatcher(
                    PathPatternParserServerWebExchangeMatcher("/login/oauth2/code/{registrationId}"),
                )
                // 認証成功時、Cookieから保存しておいたリダイレクト先に遷移させるカスタムハンドラ
                oauth2.authenticationSuccessHandler(customAuthenticationSuccessHandler)
                oauth2.authenticationFailureHandler { webFilterExchange, exception ->
                    val response = webFilterExchange.exchange.response
                    response.statusCode = org.springframework.http.HttpStatus.UNAUTHORIZED
                    response.headers.contentType = org.springframework.http.MediaType.APPLICATION_JSON
                    val body = """{"error":"${exception.javaClass.simpleName}","message":"${exception.message}"}"""
                    val buffer = response.bufferFactory().wrap(body.toByteArray(Charsets.UTF_8))
                    response.writeWith(Mono.just(buffer))
                }
            }
            // ログアウトの設定
            .logout { logout ->
                logout.logoutHandler(
                    DelegatingServerLogoutHandler(
                        SecurityContextServerLogoutHandler(),
                        WebSessionServerLogoutHandler(),
                    ),
                )
                logout.logoutSuccessHandler(customLogoutSuccessHandler)
                logout.logoutUrl("/logout")
            }
            // CSRF 対策の設定
            .csrf { csrf ->
                // SPAから参照できるよう、HttpOnly=false でクッキーにトークンを保存
                csrf.csrfTokenRepository(
                    CookieServerCsrfTokenRepository.withHttpOnlyFalse().apply {
                        setCookiePath("/")
                    },
                )
                // 生トークンをそのまま受け付けるServerCsrfTokenRequestAttributeHandlerに変更
                csrf.csrfTokenRequestHandler(ServerCsrfTokenRequestAttributeHandler())
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
                // ヘルスチェック(actuator)、認証エンドポイント、ログイン・ログアウト関連は認証不要
                authorize.pathMatchers("/actuator/**", "/oauth2/**", "/login/**", "/logout").permitAll()
                // 上記以外はすべて認証が必要
                authorize.anyExchange().authenticated()
            }
            // 認証処理の直前でリダイレクトパスをCookieに保存するカスタムフィルターを挿入
            .addFilterBefore(
                RedirectUriCookieFilter(appProperties),
                SecurityWebFiltersOrder.AUTHENTICATION,
            ).build()

    /**
     * WebFlux の CSRF トークン遅延評価を回避し、常に XSRF-TOKEN Cookie を発行・更新させるフィルター。
     */
    @Bean
    fun csrfCookieWebFilter(): WebFilter =
        WebFilter { exchange: ServerWebExchange, chain: WebFilterChain ->
            val csrfTokenMono = exchange.getAttribute<Mono<CsrfToken>>(CsrfToken::class.java.name)
            csrfTokenMono?.then(chain.filter(exchange)) ?: chain.filter(exchange)
        }

    /**
     * CORSの設定を構築します。
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
