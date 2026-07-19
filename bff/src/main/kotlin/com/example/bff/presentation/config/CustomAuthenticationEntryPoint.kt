package com.example.bff.presentation.config

import org.slf4j.LoggerFactory
import org.slf4j.MDC
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 * 未認証アクセス時に 401 Unauthorized を返すカスタムエントリポイント。
 *
 * [背景・目的]
 * Spring Securityのデフォルト挙動（ログイン画面への302リダイレクトなど）を防ぎ、
 * フロントエンド(SPA)がエラーを直接ハンドリングできるようJSONレスポンスを返す。
 *
 * [利用シーン]
 * - 未ログイン状態での保護エンドポイントへのアクセス
 * - セッションタイムアウト、またはアクセストークンの失効
 *
 * [レスポンス形式]
 * ```json
 * {
 *   "authenticated": false,
 *   "message": "Unauthorized"
 * }
 * ```
 */
class CustomAuthenticationEntryPoint : ServerAuthenticationEntryPoint {
    private val log = LoggerFactory.getLogger(CustomAuthenticationEntryPoint::class.java)

    /**
     * 認証に失敗、または未認証の状態でアクセスがあった際にSpring Securityから呼び出される処理。
     * リダイレクトは行わず、HTTPステータス 401 とエラー詳細のJSONを直接レスポンスに書き込む。
     *
     * @param exchange 現在のHTTPリクエストおよびレスポンスのコンテキスト
     * @param exception 発生した認証エラーの例外情報（トークン未設定や失効などの原因を示す）
     * @return レスポンスの書き込み完了を通知する非同期パブリッシャー (Mono<Void>)
     */
    override fun commence(
        exchange: ServerWebExchange,
        exception: AuthenticationException,
    ): Mono<Void> {
        log.info(
            "Authentication failed. path={} trace_id={} user_id={} exception={}: {}",
            exchange.request.path,
            MDC.get("trace_id") ?: "no-trace",
            MDC.get("user_id") ?: "anonymous",
            exception::class.java.name,
            exception.message,
        )

        // レスポンスオブジェクトの取得
        val response = exchange.response

        // HTTPステータスコードを 401 (Unauthorized) に設定
        response.statusCode = HttpStatus.UNAUTHORIZED
        // フロントエンドがJSONとして解釈できるようにContent-Typeを指定
        response.headers.contentType = MediaType.APPLICATION_JSON

        // クライアントに返却するJSON形式のエラーメッセージを定義
        val body = """{"authenticated":false,"message":"Unauthorized"}"""

        // 独自にファクトリを生成するのではなく、レスポンスが持っているサーバー最適化済みのファクトリを利用する
        val dataBuffer = response.bufferFactory().wrap(body.toByteArray(Charsets.UTF_8))
        
        // レスポンスのボディにデータを書き込み、非同期処理の完了(Mono<Void>)として返却する
        return response.writeWith(Mono.just(dataBuffer))
    }
}
