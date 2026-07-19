package com.example.bff.presentation.dto

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * [AuthResponse] のテストクラス。
 *
 * 認証状態レスポンスDTOのデフォルト値および値保持を検証します。
 */
class AuthResponseTest {

    @Test
    @DisplayName("認証済みの全項目を保持できること")
    fun holdsAllFieldsWhenAuthenticated() {
        val response = AuthResponse(
            authenticated = true,
            userId = "sub-001",
            username = "admin",
            roles = listOf("ADMIN", "USER"),
        )

        assertThat(response.authenticated).isTrue()
        assertThat(response.userId).isEqualTo("sub-001")
        assertThat(response.username).isEqualTo("admin")
        assertThat(response.roles).containsExactly("ADMIN", "USER")
    }

    @Test
    @DisplayName("未認証時は任意項目がnullのデフォルト値になること")
    fun defaultsNullableFieldsToNull() {
        val response = AuthResponse(authenticated = false)

        assertThat(response.authenticated).isFalse()
        assertThat(response.userId).isNull()
        assertThat(response.username).isNull()
        assertThat(response.roles).isNull()
    }

    @Test
    @DisplayName("同じ値のインスタンスは等価であること")
    fun equalityHoldsForSameValues() {
        val a = AuthResponse(authenticated = true, userId = "x")
        val b = AuthResponse(authenticated = true, userId = "x")

        assertThat(a).isEqualTo(b)
        assertThat(a.hashCode()).isEqualTo(b.hashCode())
    }

    @Test
    @DisplayName("copyで一部項目のみ変更できること")
    fun copyChangesSelectedFields() {
        val original = AuthResponse(authenticated = false)

        val updated = original.copy(authenticated = true, username = "user1")

        assertThat(updated.authenticated).isTrue()
        assertThat(updated.username).isEqualTo("user1")
        assertThat(original.authenticated).isFalse()
    }
}
