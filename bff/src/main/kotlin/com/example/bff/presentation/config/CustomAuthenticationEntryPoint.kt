package com.example.bff.presentation.config

import org.springframework.core.io.buffer.DefaultDataBufferFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.server.ServerAuthenticationEntryPoint
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import java.nio.charset.StandardCharsets

/**
 * 未認証アクセス時に 401 Unauthorized を返すカスタムエントリポイント。
 * トークン・セッション失効時を含むすべての未認証ケースで使用される。
 *
 * レスポンス形式:
 * ```json
 * { "authenticated": false, "message": "Unauthorized" }
 * ```
 */
class CustomAuthenticationEntryPoint : ServerAuthenticationEntryPoint {

    override fun commence(
        exchange: ServerWebExchange,
        exception: AuthenticationException
    ): Mono<Void> {
        val response = exchange.response
        response.statusCode = HttpStatus.UNAUTHORIZED
        response.headers.contentType = MediaType.APPLICATION_JSON

        val body = """{"authenticated":false,"message":"Unauthorized"}"""
        val dataBuffer = DefaultDataBufferFactory().wrap(body.toByteArray(StandardCharsets.UTF_8))

        return response.writeWith(Mono.just(dataBuffer))
    }
}