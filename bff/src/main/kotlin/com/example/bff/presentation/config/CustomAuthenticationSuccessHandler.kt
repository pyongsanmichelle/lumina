package com.example.bff.presentation.config

import com.example.bff.integration.config.AppProperties
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.springframework.web.util.UriComponentsBuilder
import reactor.core.publisher.Mono

/**
 * OIDC認証成功時のカスタムリダイレクトハンドラ。
 *
 * [RedirectUriCookieFilter] が保存した `POST_LOGIN_REDIRECT_URI` Cookieの値を読み取り、
 * 存在すればそのパスへ、存在しなければデフォルトの `${FRONTEND_ORIGIN}/` へ
 * 絶対URLでリダイレクトします（認証フロー仕様書 5.1節）。
 *
 * Cookieの値は改ざんされている可能性があるため、ここでも再度
 * [RedirectUriCookieFilter.validateRedirectUri] によるバリデーションを行います。
 *
 * @property appProperties アプリケーション設定（FRONTEND_ORIGINの取得に使用）
 */
@Component
class CustomAuthenticationSuccessHandler(
    private val appProperties: AppProperties,
) : ServerAuthenticationSuccessHandler {

    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication,
    ): Mono<Void> {
        val exchange = webFilterExchange.exchange
        val savedCookie = exchange.request.cookies
            .getFirst(RedirectUriCookieFilter.POST_LOGIN_REDIRECT_URI_COOKIE)

        // Cookie不在、または改ざん等で不正な値の場合はデフォルト("/")へフォールバック
        val redirectPath = RedirectUriCookieFilter.validateRedirectUri(savedCookie?.value) ?: "/"

        val targetUrl = UriComponentsBuilder
            .fromUriString(appProperties.frontendOrigin)
            .path(redirectPath)
            .build(true)
            .toUri()

        val response = exchange.response
        response.statusCode = HttpStatus.FOUND
        response.headers.location = targetUrl

        // 使い終えた一時Cookieは即座に無効化する(Max-Age=0で削除)
        if (savedCookie != null) {
            val expired = ResponseCookie.from(RedirectUriCookieFilter.POST_LOGIN_REDIRECT_URI_COOKIE, "")
                .httpOnly(true)
                .secure(appProperties.frontendOrigin.startsWith("https"))
                .sameSite("Lax")
                .path("/")
                .maxAge(0)
                .build()
            response.addCookie(expired)
        }

        return response.setComplete()
    }
}