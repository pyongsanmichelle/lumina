package com.example.api.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserSearchConditionのテスト")
class UserSearchConditionTest {

    @Test
    @DisplayName("各アクセサが設定した値を返す")
    void 各アクセサが設定した値を返す() {
        UserSearchCondition condition =
            new UserSearchCondition("管理者", "admin@", UserStatus.ENABLED);

        assertThat(condition.name()).isEqualTo("管理者");
        assertThat(condition.email()).isEqualTo("admin@");
        assertThat(condition.status()).isEqualTo(UserStatus.ENABLED);
    }

    @Test
    @DisplayName("nullを許容する")
    void nullを許容する() {
        UserSearchCondition condition = new UserSearchCondition(null, null, null);

        assertThat(condition.name()).isNull();
        assertThat(condition.email()).isNull();
        assertThat(condition.status()).isNull();
    }

    @Test
    @DisplayName("同じ値のレコードは等価である")
    void 同じ値のレコードは等価である() {
        UserSearchCondition a = new UserSearchCondition("管理者", "admin@", UserStatus.ENABLED);
        UserSearchCondition b = new UserSearchCondition("管理者", "admin@", UserStatus.ENABLED);

        assertThat(a).isEqualTo(b);
        assertThat(a).hasSameHashCodeAs(b);
    }
}
