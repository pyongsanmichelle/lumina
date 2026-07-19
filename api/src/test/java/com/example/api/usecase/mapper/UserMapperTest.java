package com.example.api.usecase.mapper;

import com.example.api.domain.User;
import com.example.api.domain.UserStatus;
import com.example.api.presentation.response.UserResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("UserMapperのテスト")
class UserMapperTest {

    private final UserMapper mapper = new UserMapperImpl();

    private User createUser() {
        OffsetDateTime created = OffsetDateTime.of(2024, 7, 8, 10, 34, 0, 0, ZoneOffset.ofHours(9));
        OffsetDateTime updated = OffsetDateTime.of(2024, 7, 9, 12, 0, 0, 0, ZoneOffset.ofHours(9));
        User user = new User();
        user.setId(1L);
        user.setIdpSubject("sso-user-uuid-0001");
        user.setEmail("admin@example.com");
        user.setName("管理者");
        user.setTimezone("Asia/Tokyo");
        user.setVersion(2L);
        user.setStatus(UserStatus.ENABLED);
        user.setCreatedBy(0L);
        user.setCreatedAt(created);
        user.setUpdatedBy(0L);
        user.setUpdatedAt(updated);
        return user;
    }

    @Test
    @DisplayName("UserからUserResponseへ変換し、日時はISO形式の文字列になる")
    void UserからUserResponseへ変換し日時はISO形式の文字列になる() {
        UserResponse response = mapper.toResponse(createUser());

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getIdpSubject()).isEqualTo("sso-user-uuid-0001");
        assertThat(response.getEmail()).isEqualTo("admin@example.com");
        assertThat(response.getName()).isEqualTo("管理者");
        assertThat(response.getTimezone()).isEqualTo("Asia/Tokyo");
        assertThat(response.getVersion()).isEqualTo(2L);
        assertThat(response.getStatus()).isEqualTo("ENABLED");
        assertThat(response.getCreatedAt()).isEqualTo("2024-07-08T10:34:00+09:00");
        assertThat(response.getUpdatedAt()).isEqualTo("2024-07-09T12:00:00+09:00");
    }

    @Test
    @DisplayName("nullのUserはnullに変換される")
    void nullのUserはnullに変換される() {
        assertThat(mapper.toResponse(null)).isNull();
    }

    @Test
    @DisplayName("日時がnullのUserは日時フィールドがnullになる")
    void 日時がnullのUserは日時フィールドがnullになる() {
        User user = createUser();
        user.setCreatedAt(null);
        user.setUpdatedAt(null);

        UserResponse response = mapper.toResponse(user);

        assertThat(response.getCreatedAt()).isNull();
        assertThat(response.getUpdatedAt()).isNull();
    }

    @Test
    @DisplayName("リスト変換ができる")
    void リスト変換ができる() {
        List<UserResponse> responses = mapper.toResponseList(List.of(createUser()));

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getName()).isEqualTo("管理者");
    }

    @Test
    @DisplayName("nullリストはnullに変換される")
    void nullリストはnullに変換される() {
        assertThat(mapper.toResponseList(null)).isNull();
    }

    @Test
    @DisplayName("map(OffsetDateTime)はnull安全である")
    void mapはnull安全である() {
        assertThat(mapper.map(null)).isNull();
        OffsetDateTime dt = OffsetDateTime.of(2024, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        assertThat(mapper.map(dt)).isEqualTo(dt.toString());
    }
}
