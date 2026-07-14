package com.example.bff.integration.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.client.WebClient

/**
 * API（api/）疎通用のWebClient Beanを定義するConfigurationクラス。
 *
 * @property apiBaseUrl 環境変数 APP_API_BASE_URL から注入されるAPIベースURL（デフォルト: http://api:8081/api/v1）
 */
@Configuration
class WebClientConfig(
    @Value("\${app.api.base-url:http://api:8081/api/v1}")
    private val apiBaseUrl: String
) {
    @Bean
    fun apiWebClient(): WebClient {
        return WebClient.builder()
            .baseUrl(apiBaseUrl)
            .build()
    }
}