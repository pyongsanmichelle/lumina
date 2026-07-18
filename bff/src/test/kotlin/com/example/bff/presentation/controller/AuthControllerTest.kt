package com.example.bff.presentation.controller

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import reactor.test.StepVerifier

/**
 * [AuthController] のテストクラス。
 * 
 * セキュリティコンテキストから取得したユーザー情報が、
 * APIレスポンスとして正しくマッピングされるかを検証します。
 */
@ExtendWith(MockitoExtension::class)
class AuthControllerTest {

    private val controller = AuthController()

    /**
     * 認証済みユーザーがアクセスした際、ユーザーIDやロール情報が
     * 正しくレスポンスDTOに反映されることを確認します。
     */
    @Test
    @DisplayName("getAuthStatus: 認証済みなら authenticated=true と詳細なユーザー情報を返すこと")
    fun getAuthStatus_authenticated_returnsAuthResponseWithUserInfo() {
        // Arrange: モック化された OidcUser の準備
        val principal = Mockito.mock(OidcUser::class.java)
        Mockito.`when`(principal.subject).thenReturn("user-123")
        Mockito.`when`(principal.preferredUsername).thenReturn("testuser")
        Mockito.`when`(principal.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("ROLE_USER"),
                SimpleGrantedAuthority("ROLE_ADMIN")
            )
        )

        // Act: コントローラーメソッドの呼び出し
        val result = controller.getAuthStatus(principal)

        // Assert: レスポンス内容の検証（AssertJを使用）
        StepVerifier.create(result)
            .assertNext { response ->
                assertThat(response.authenticated).isTrue()
                assertThat(response.userId).isEqualTo("user-123")
                assertThat(response.username).isEqualTo("testuser")
                assertThat(response.roles).containsExactly("ROLE_USER", "ROLE_ADMIN")
            }
            .verifyComplete()
    }

    /**
     * 未認証（principalがnull）の場合、認証状態が false で
     * ユーザー情報が空であることを確認します。
     */
    @Test
    @DisplayName("getAuthStatus: 未認証なら authenticated=false と空のレスポンスを返すこと")
    fun getAuthStatus_unauthenticated_returnsAuthResponseWithNullFields() {
        // Act
        val result = controller.getAuthStatus(null)

        // Assert
        StepVerifier.create(result)
            .assertNext { response ->
                assertThat(response.authenticated).isFalse()
                assertThat(response.userId).isNull()
                assertThat(response.username).isNull()
                assertThat(response.roles).isNull()
            }
            .verifyComplete()
    }
}