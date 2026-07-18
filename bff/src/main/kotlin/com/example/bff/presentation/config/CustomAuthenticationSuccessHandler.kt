package com.example.bff.presentation.config

import com.example.bff.integration.config.AppProperties
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie // ★ 追加
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.WebFilterExchange
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationSuccessHandler
import reactor.core.publisher.Mono
import java.net.URI

/**
 * カスタム認証成功ハンドラ。
 *
 * OAuth2などのログイン認証が成功した後に実行される処理を定義します。
 * 認証開始前に `POST_LOGIN_REDIRECT_URI` Cookie に保存しておいた遷移先パスを読み取り、
 * `${FRONTEND_ORIGIN}` を付与した絶対URLを構築してフロントエンドへリダイレクトさせます。
 * 
 * 相対パスのままリダイレクトするとBFFのオリジン上で解決されてしまうため、
 * 必ずフロントエンドのオリジンを明示的に指定する役割を持ちます。
 *
 * @property appProperties アプリケーション設定（フロントエンドのオリジンURLなどを保持）
 */
class CustomAuthenticationSuccessHandler(
    private val appProperties: AppProperties
) : RedirectServerAuthenticationSuccessHandler() {

    /**
     * 認証成功時のリダイレクト処理を実行します。
     *
     * @param webFilterExchange WebFilterの実行コンテキスト（リクエストやレスポンスへのアクセスを提供）
     * @param authentication 成功した認証情報（ユーザー情報や権限など）
     * @return 処理の完了を通知するMono<Void>
     */
    override fun onAuthenticationSuccess(
        webFilterExchange: WebFilterExchange,
        authentication: Authentication
    ): Mono<Void> {
        // 現在のリクエストおよびレスポンスオブジェクトを取得
        val exchange = webFilterExchange.exchange
        val response = exchange.response

        // 復帰先パスの取得
        // クライアントから送信されたCookieから POST_LOGIN_REDIRECT_URI を検索。
        // Cookieが存在しない場合（直接ログインURLを叩いた場合など）はデフォルト値として "/" を使用する。
        val redirectPath = exchange.request.cookies
            .getFirst(RedirectUriCookieFilter.POST_LOGIN_REDIRECT_URI_COOKIE)
            ?.value
            ?: "/"

        // リダイレクト先URLの組み立て
        // フロントエンドのオリジンと復帰先パスを結合して絶対URLを作成する。
        val redirectUrl = "${appProperties.frontendOrigin}$redirectPath"

        // リダイレクト用レスポンスの設定
        // HTTPステータス 302 (Found) を設定し、Locationヘッダに遷移先URLを指定する。
        response.statusCode = HttpStatus.FOUND
        response.headers.location = URI.create(redirectUrl)

        // 使用済みCookieのクリーンアップ処理
        // 寿命(maxAge)を0にした空のCookieをSet-Cookieヘッダに乗せて送信し、ブラウザから削除させる。
        // ※発行時と同じpathを指定しないと消えない場合があるため .path("/") を付与
        val deleteCookie = ResponseCookie.from(RedirectUriCookieFilter.POST_LOGIN_REDIRECT_URI_COOKIE, "")
            .maxAge(0)
            .path("/") 
            .build()
        response.addCookie(deleteCookie)

        // 処理の完了
        // レスポンスの書き込みを終了し、非同期処理チェーンを完了させる。
        return response.setComplete()
    }
}