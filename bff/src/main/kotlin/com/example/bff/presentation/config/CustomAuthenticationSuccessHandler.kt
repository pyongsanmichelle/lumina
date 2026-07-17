package com.example.bff.presentation.config

import com.example.bff.integration.config.AppProperties
import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.net.URI

/**
 * カスタム認証成功ハンドラ。
 * 認証成功時に `POST_LOGIN_REDIRECT_URI` Cookie を読み取り、
 * そのパスへ `${FRONTEND_ORIGIN}` を付与した絶対URLでリダイレクトする。
 *
 * Cookie が存在しない場合はデフォルトの `${FRONTEND_ORIGIN}/` へリダイレクトする。
 * 相対パスでのリダイレクトは禁止（BFF自身のオリジンとして解決されるのを防ぐため）。
 */
class CustomAuthenticationSuccessHandler(
    private val appProperties: AppProperties
) : RedirectServerAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        val exchange = webFilterExchange.exchange
        val response = exchange.response

        // POST_LOGIN_REDIRECT_URI Cookie から復帰先パスを取得
        val redirectPath = exchange.request.cookies
            .getFirst(RedirectUriCookieFilter.POST_LOGIN_REDIRECT_URI_COOKIE)
            ?.value
            ?: "/" // Cookie がなければデフォルト

        // 絶対URLを組み立て（FRONTEND_ORIGIN を付与）
        val redirectUrl = "${appProperties.frontendOrigin}$redirectPath"

        // リダイレクト実行
        response.statusCode = HttpStatus.FOUND
        response.headers.location = URI.create(redirectUrl)

        // POST_LOGIN_REDIRECT_URI Cookie を削除（使い捨て）
        response.cookies.remove(RedirectUriCookieFilter.POST_LOGIN_REDIRECT_URI_COOKIE)

        return response.setComplete()
    }
}