package com.example.api.usecase.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ResourceNotFoundExceptionのテスト")
class ResourceNotFoundExceptionTest {

    @Test
    @DisplayName("リソース名と識別子からメッセージを自動生成する")
    void リソース名と識別子からメッセージを自動生成する() {
        ResourceNotFoundException ex = new ResourceNotFoundException("User", 42L);

        assertThat(ex.getMessage()).isEqualTo("User not found with id: 42");
    }

    @Test
    @DisplayName("カスタムメッセージをそのまま保持する")
    void カスタムメッセージをそのまま保持する() {
        ResourceNotFoundException ex = new ResourceNotFoundException("custom message");

        assertThat(ex.getMessage()).isEqualTo("custom message");
    }

    @Test
    @DisplayName("RuntimeExceptionのサブクラスである")
    void RuntimeExceptionのサブクラスである() {
        assertThat(new ResourceNotFoundException("x")).isInstanceOf(RuntimeException.class);
    }
}
