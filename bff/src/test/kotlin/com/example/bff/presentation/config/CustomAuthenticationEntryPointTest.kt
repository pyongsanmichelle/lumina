package com.example.bff.presentation.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.security.core.AuthenticationException
import reactor.test.StepVerifier
import java.nio.charset.StandardCharsets

/**
 * [CustomAuthenticationEntryPoint] のテストクラス。
 * 
 * 未認証アクセスが発生した際に、適切な HTTP ステータスコード（401）と
 * APIとしてのレスポンス形式（JSON）が返却されることを検証します。
 */
class CustomAuthenticationEntryPointTest {

    private val entryPoint = CustomAuthenticationEntryPoint()

    /**
     * 未認証例外が発生した際に、クライアントが期待するJSONエラーレスポンスが
     * 正しく返却されることを検証します。
     */
    @Test
    @DisplayName("commence() が 401 Unauthorized と期待通りのJSONレスポンスを返すこと")
    fun commence_returns401UnauthorizedWithJsonBody() {
        // Arrange (準備): モックリクエストと例外を生成
        val request = MockServerHttpRequest.get("/auth/me").build()
        val exchange = MockServerWebExchange.from(request)
        val exception = mock(AuthenticationException::class.java)

        // Act (実行): エントリポイントの処理を呼び出し
        val result = entryPoint.commence(exchange, exception)

        // Assert (検証)
        // 非同期処理が完了するまで待機
        StepVerifier.create(result)
            .verifyComplete()

        // レスポンスの検証
        val response = exchange.response
        
        // ステータスコードが 401 であること
        assertThat(response.statusCode).isEqualTo(HttpStatus.UNAUTHORIZED)
        
        // Content-Type が application/json であること
        assertThat(response.headers.contentType).isEqualTo(MediaType.APPLICATION_JSON)

        // ボディ内容の検証: JSON文字列が一致するか確認
        val bodyContent = response.body
        StepVerifier.create(bodyContent)
            .consumeNextWith { dataBuffer ->
                val bytes = ByteArray(dataBuffer.readableByteCount())
                dataBuffer.read(bytes)
                val json = String(bytes, StandardCharsets.UTF_8)
                
                // 期待されるJSON構造と比較
                assertThat(json).isEqualTo("""{"authenticated":false,"message":"Unauthorized"}""")
            }
            .verifyComplete()
    }
}