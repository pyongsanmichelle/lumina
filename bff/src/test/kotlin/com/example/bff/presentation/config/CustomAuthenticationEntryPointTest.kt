package com.example.bff.presentation.config

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.http.server.reactive.MockServerHttpRequest
import org.springframework.mock.web.server.MockServerWebExchange
import org.springframework.security.core.AuthenticationException
import reactor.test.StepVerifier

class CustomAuthenticationEntryPointTest {

    private val entryPoint = CustomAuthenticationEntryPoint()

    @Test
    @DisplayName("commence() が 401 Unauthorized と JSON Content-Type を返すこと")
    fun commence_returns401UnauthorizedWithJsonContentType() {
        val request = MockServerHttpRequest.get("/auth/me").build()
        val exchange = MockServerWebExchange.from(request)
        val exception = mock(AuthenticationException::class.java)

        val result = entryPoint.commence(exchange, exception)

        StepVerifier.create(result)
            .verifyComplete()

        val response = exchange.response
        assert(response.statusCode == HttpStatus.UNAUTHORIZED) {
            "Expected 401 UNAUTHORIZED but got ${response.statusCode}"
        }
        assert(response.headers.contentType == MediaType.APPLICATION_JSON) {
            "Expected Content-Type application/json but got ${response.headers.contentType}"
        }
    }
}