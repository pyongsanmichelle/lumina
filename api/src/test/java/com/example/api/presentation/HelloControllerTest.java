package com.example.api.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HelloControllerのテスト")
class HelloControllerTest {

    private final HelloController controller = new HelloController();

    @Test
    @DisplayName("固定のメッセージを返す")
    void 固定のメッセージを返す() {
        Map<String, String> result = controller.hello();

        assertThat(result).containsEntry("message", "Hello from API");
    }
}
