package com.example.bff.integration.config

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * [AppProperties] のテストクラス。
 *
 * 構成プロパティDTOが各値を正しく保持することを検証します。
 */
class AppPropertiesTest {

    private fun sample() = AppProperties(
        frontendOrigin = "http://localhost:3000",
        bffOrigin = "http://localhost:8081",
        apiBaseUrl = "http://localhost:8080",
        keycloakLogoutUrl = "http://localhost:8180/logout",
    )

    @Test
    @DisplayName("全ての設定値を保持できること")
    fun holdsAllProperties() {
        val props = sample()

        assertThat(props.frontendOrigin).isEqualTo("http://localhost:3000")
        assertThat(props.bffOrigin).isEqualTo("http://localhost:8081")
        assertThat(props.apiBaseUrl).isEqualTo("http://localhost:8080")
        assertThat(props.keycloakLogoutUrl).isEqualTo("http://localhost:8180/logout")
    }

    @Test
    @DisplayName("同じ値のインスタンスは等価であること")
    fun equalityHoldsForSameValues() {
        assertThat(sample()).isEqualTo(sample())
        assertThat(sample().hashCode()).isEqualTo(sample().hashCode())
    }

    @Test
    @DisplayName("copyで一部項目のみ変更できること")
    fun copyChangesSelectedFields() {
        val updated = sample().copy(apiBaseUrl = "http://api:9090")

        assertThat(updated.apiBaseUrl).isEqualTo("http://api:9090")
        assertThat(updated.frontendOrigin).isEqualTo("http://localhost:3000")
    }
}
