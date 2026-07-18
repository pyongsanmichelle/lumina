package com.example.bff.presentation.controller

import com.example.bff.presentation.dto.AuthResponse
import org.springframework.http.MediaType
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

/**
 * 認証状態確認用のAPIコントローラー。
 * 
 * ユーザーが現在ログインしているかを確認し、認証済みであればユーザー情報を含めたレスポンスを返します。
 */
@RestController
class AuthController {

    /**
     * ログイン中のユーザー情報を取得します。
     * 
     * @param principal 認証情報（未ログイン時はnull）
     * @return 認証状態とユーザー情報を含む [AuthResponse]
     */
    @GetMapping("/auth/me", produces = [MediaType.APPLICATION_JSON_VALUE])
    fun getAuthStatus(
        @AuthenticationPrincipal principal: OidcUser?
    ): Mono<AuthResponse> = Mono.just(
        if (principal != null) {
            // 認証済みの場合：ユーザー詳細情報を設定
            AuthResponse(
                authenticated = true,
                userId = principal.subject,
                username = principal.preferredUsername,
                roles = principal.authorities.mapNotNull { it.authority }
            )
        } else {
            // 未認証の場合：認証フラグのみ false で設定
            AuthResponse(authenticated = false)
        }
    )
}