package com.example.api.domain;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Userエンティティのテスト")
class UserTest {

    @Test
    @DisplayName("デフォルト値が設定されている")
    void デフォルト値が設定されている() {
        User user = new User();

        assertThat(user.getTimezone()).isEqualTo("Asia/Tokyo");
        assertThat(user.getVersion()).isEqualTo(0L);
        assertThat(user.getStatus()).isEqualTo(UserStatus.ENABLED);
        assertThat(user.getCreatedBy()).isEqualTo(0L);
        assertThat(user.getUpdatedBy()).isEqualTo(0L);
    }

    @Test
    @DisplayName("setterで設定した値をgetterで取得できる")
    void setterで設定した値をgetterで取得できる() {
        OffsetDateTime now = OffsetDateTime.now();
        User user = new User();

        user.setId(10L);
        user.setIdpSubject("sso-user-uuid-0001");
        user.setEmail("admin@example.com");
        user.setName("管理者");
        user.setTimezone("UTC");
        user.setVersion(3L);
        user.setStatus(UserStatus.DISABLED);
        user.setCreatedBy(1L);
        user.setCreatedAt(now);
        user.setUpdatedBy(2L);
        user.setUpdatedAt(now);

        assertThat(user.getId()).isEqualTo(10L);
        assertThat(user.getIdpSubject()).isEqualTo("sso-user-uuid-0001");
        assertThat(user.getEmail()).isEqualTo("admin@example.com");
        assertThat(user.getName()).isEqualTo("管理者");
        assertThat(user.getTimezone()).isEqualTo("UTC");
        assertThat(user.getVersion()).isEqualTo(3L);
        assertThat(user.getStatus()).isEqualTo(UserStatus.DISABLED);
        assertThat(user.getCreatedBy()).isEqualTo(1L);
        assertThat(user.getCreatedAt()).isEqualTo(now);
        assertThat(user.getUpdatedBy()).isEqualTo(2L);
        assertThat(user.getUpdatedAt()).isEqualTo(now);
    }

    @Test
    @DisplayName("toStringにフィールド値が含まれる")
    void toStringにフィールド値が含まれる() {
        User user = new User();
        user.setName("管理者");

        assertThat(user.toString()).contains("管理者");
    }
}
