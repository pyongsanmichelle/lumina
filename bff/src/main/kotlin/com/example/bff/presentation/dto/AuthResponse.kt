package com.example.bff.presentation.dto

/**
 * 認証状態確認API（GET /bff/auth/me）のレスポンスDTO。
 */
data class AuthResponse(
    val authenticated: Boolean,
    val userId: String? = null,
    val username: String? = null,
    val roles: List<String>? = null
)