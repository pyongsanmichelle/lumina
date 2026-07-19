package com.example.bff.integration.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import org.springframework.web.reactive.function.client.WebClient

/**
 * [WebClientConfig] のテストクラス。
 *
 * apiWebClient Bean が [AppProperties] のベースURLで構築されることを検証します。
 */
class WebClientConfigTest {

    private val appProperties = AppProperties(
        frontendOrigin = "http://localhost:3000",
        bffOrigin = "http://localhost:8081",
        apiBaseUrl = "http://api:8080",
        keycloakLogoutUrl = "http://localhost:8180/logout",
    )

    @Test
    @DisplayName("apiWebClientがapiBaseUrlをベースURLに設定して構築すること")
    fun apiWebClientUsesApiBaseUrl() {
        val builder = mock<WebClient.Builder>()
        val webClient = mock<WebClient>()
        whenever(builder.baseUrl(any())).thenReturn(builder)
        whenever(builder.build()).thenReturn(webClient)

        val result = WebClientConfig(appProperties).apiWebClient(builder)

        assertThat(result).isSameAs(webClient)
        verify(builder).baseUrl("http://api:8080")
        verify(builder).build()
    }
}
