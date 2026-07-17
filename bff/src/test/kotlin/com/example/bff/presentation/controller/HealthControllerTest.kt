package com.example.bff.presentation.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

class HealthControllerTest {

    private val controller = HealthController()

    @Test
    @DisplayName("healthDebug() が status=ok を返すこと")
    fun healthDebug_returnsStatusOk() {
        val result = controller.healthDebug()

        assertThat(result)
            .containsEntry("status", "ok")
    }
}