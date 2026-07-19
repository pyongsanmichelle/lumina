package com.example.bff.presentation.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * [HealthController] のテストクラス。
 *
 * システムのヘルスチェックエンドポイントが正しく稼働状況を返すことを検証します。
 */
class HealthControllerTest {
    private val controller = HealthController()

    /**
     * ヘルスチェックが期待通りのレスポンスオブジェクトを返すことを確認します。
     */
    @Test
    @DisplayName("healthDebug() が status=ok を含むレスポンスを返すこと")
    fun healthDebug_returnsStatusOk() {
        // Act: コントローラーメソッドを実行
        val result = controller.healthDebug()

        // Assert: レスポンスの型と中身を検証
        assertThat(result).isInstanceOf(HealthResponse::class.java)
        assertThat(result.status).isEqualTo("ok")
    }
}
