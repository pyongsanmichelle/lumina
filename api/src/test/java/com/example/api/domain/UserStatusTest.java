package com.example.api.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserStatusのテスト")
class UserStatusTest {

    @Test
    @DisplayName("各値が想定された説明を持つ")
    void 各値が想定された説明を持つ() {
        assertThat(UserStatus.ENABLED.getDescription()).isEqualTo("有効");
        assertThat(UserStatus.DISABLED.getDescription()).isEqualTo("無効");
        assertThat(UserStatus.DELETED.getDescription()).isEqualTo("削除");
    }

    @Test
    @DisplayName("valueOfで文字列から列挙値を取得できる")
    void valueOfで文字列から列挙値を取得できる() {
        assertThat(UserStatus.valueOf("ENABLED")).isEqualTo(UserStatus.ENABLED);
    }

    @Test
    @DisplayName("列挙値は3種類である")
    void 列挙値は3種類である() {
        assertThat(UserStatus.values()).containsExactly(
            UserStatus.ENABLED, UserStatus.DISABLED, UserStatus.DELETED);
    }
}
