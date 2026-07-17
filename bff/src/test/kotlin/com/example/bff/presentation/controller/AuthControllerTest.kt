package com.example.bff.presentation.controller

import com.example.bff.presentation.dto.AuthResponse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import reactor.test.StepVerifier

@ExtendWith(MockitoExtension::class)
class AuthControllerTest {

    private val controller = AuthController()

    @Test
    @DisplayName("getAuthStatus 認証済みの場合 authenticated=true とユーザー情報が返ること")
    fun getAuthStatus_authenticated_returnsAuthResponseWithUserInfo() {
        val principal = Mockito.mock(OidcUser::class.java)
        Mockito.`when`(principal.subject).thenReturn("user-123")
        Mockito.`when`(principal.preferredUsername).thenReturn("testuser")
        Mockito.`when`(principal.authorities).thenReturn(
            listOf(
                SimpleGrantedAuthority("ROLE_USER"),
                SimpleGrantedAuthority("ROLE_ADMIN")
            )
        )

        val result = controller.getAuthStatus(principal)

        StepVerifier.create(result)
            .assertNext { response ->
                assert(response.authenticated) { "Expected authenticated=true" }
                assert(response.userId == "user-123") {
                    "Expected userId='user-123' but got '${response.userId}'"
                }
                assert(response.username == "testuser") {
                    "Expected username='testuser' but got '${response.username}'"
                }
                assert(response.roles == listOf("ROLE_USER", "ROLE_ADMIN")) {
                    "Expected roles=[ROLE_USER, ROLE_ADMIN] but got ${response.roles}"
                }
            }
            .verifyComplete()
    }

    @Test
    @DisplayName("getAuthStatus 未認証の場合 authenticated=false とnull値が返ること")
    fun getAuthStatus_unauthenticated_returnsAuthResponseWithNullFields() {
        val result = controller.getAuthStatus(null)

        StepVerifier.create(result)
            .assertNext { response ->
                assert(!response.authenticated) { "Expected authenticated=false" }
                assert(response.userId == null) { "Expected userId=null but got '${response.userId}'" }
                assert(response.username == null) { "Expected username=null but got '${response.username}'" }
                assert(response.roles == null) { "Expected roles=null but got ${response.roles}" }
            }
            .verifyComplete()
    }
}