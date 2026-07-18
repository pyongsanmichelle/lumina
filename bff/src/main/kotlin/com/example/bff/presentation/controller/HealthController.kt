package com.example.bff.presentation.controller

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

/**
 * システムの稼働状況を確認するためのヘルスチェック用コントローラー。
 */
@RestController
class HealthController {

    /**
     * システムの現在の状態を返します。
     * 
     * @return 稼働状況を示す [HealthResponse] オブジェクト
     */
    @GetMapping("/health", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun healthDebug(): HealthResponse = HealthResponse("ok")
}

/**
 * ヘルスチェック用レスポンスDTO。
 */
data class HealthResponse(
    val status: String
)